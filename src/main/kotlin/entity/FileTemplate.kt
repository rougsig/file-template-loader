package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.extension.getPackageNameWithSubDirs
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.apache.commons.io.output.NullWriter
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.event.EventCartridge
import org.apache.velocity.app.event.ReferenceInsertionEventHandler
import java.util.*

interface FileTemplate {
  val name: String
  val hasClassName: Boolean
  fun create(dir: PsiDirectory, props: Properties): List<PsiFile>

  fun getAllProps(): Set<String>

  fun getRequiredProps(props: Properties): Set<String> {
    val allProps = getAllProps()
    val propsBase = getPropsBase(allProps)
    val existedProps = props.keys as Set<String>

    val allRequiredProps = allProps.plus(propsBase).toSet().minus(existedProps)

    return allRequiredProps.filter { it != PROPS_NAME || it.isBlank() }.toSet()
  }

  fun generateProps(props: Properties)
}

fun mergeTemplate(templateText: String, props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, templateText, true)
  return StringUtil.convertLineSeparators(merged)
}

fun getTemplateProps(templateText: String): Set<String> {
  return templateText.getReferences()
    .map {
      it.replace("{", "")
        .replace("}", "")
        .replace("\$", "")
    }
    .toSet()
}

fun getPropsBase(fullNameProps: Iterable<String>): Set<String> {
  return fullNameProps.mapNotNull { prop ->
    GENERATED_PROP_MATCHER.find(prop)?.value?.let {
      GENERATED_PROP_MATCHER.replace(prop) { "" }
        // Remove last `_` from prop name
        .dropLast(1)
    }
  }.toSet()
}

fun Set<String>.filterNotGenerated(): Set<String> {
  return filterNot {
    GENERATED_PROP_MATCHER.containsMatchIn(it)
        || it.contains(PROPS_SIMPLE_NAME(""))
        || it.contains(PROPS_CLASS_NAME(""))
  }.toSet()
}

fun generateProps(propsToGenerate: Set<String>, props: Props) {
  propsToGenerate.filter { GENERATED_PROP_MATCHER.containsMatchIn(it) }
    .map { fullPropName ->
      val generatorName = GENERATED_PROP_MATCHER.find(fullPropName)!!.value
      val basePropName = GENERATED_PROP_MATCHER.replace(fullPropName) { "" }
        // Remove last `_` from prop name
        .dropLast(1)

      val propGenerator = PROP_GENERATORS.getValue(generatorName)
      val generatedProp = propGenerator(props.getProperty(basePropName))

      props.setProperty(fullPropName, generatedProp)
    }
}

fun generateProps(props: Props, templates: List<FileTemplateSingle>, getRequiredProps: (Props) -> Set<String>) {
  //
  // Generate props without _SIMPLE_NAME, _CLASS_NAME
  //
  val firstIteration = getRequiredProps(props)
    .filterNot { it.contains(PROPS_SIMPLE_NAME("")) || it.contains(PROPS_CLASS_NAME("")) }
    .toSet()
  generateProps(firstIteration, props)

  //
  // Generate _SIMPLE_NAME, _CLASS_NAME
  //
  templates.forEach { template ->
    props.setProperty(PROPS_SIMPLE_NAME(template.name), mergeTemplate(template.fileName!!, props))
  }

  //
  // Generate _SIMPLE_NAME_LOWER_CASE, _CLASS_NAME_UPPER_CASE etc
  //
  val secondIteration = getRequiredProps(props)
  generateProps(secondIteration, props)

  //
  // Generate other props
  //
  templates.forEach { it.generateProps(props) }
}

fun generateClassNameProps(props: Props, templates: List<FileTemplateSingle>, packageName: String) {
  templates.forEach { template ->
    val mergedPackageName = mergeTemplate(template.getPackageNameWithSubDirs(packageName), props)
    val fileName = mergeTemplate(template.fileName!!, props)
    if (template.hasClassName) props.setProperty(
      PROPS_CLASS_NAME(template.name),
      mergeTemplate("$mergedPackageName.$fileName", props)
    )
  }
}

private fun String.getReferences(): Set<String> {
  val names = HashSet<String>()
  val velocityContext = VelocityContext()
  val ec = EventCartridge()
  ec.addEventHandler(ReferenceInsertionEventHandler { reference, _ -> names.add(reference) })
  ec.attachToContext(velocityContext)
  Velocity.evaluate(velocityContext, NullWriter.NULL_WRITER, "", this)
  return names
}
