package com.github.rougsig.filetemplateloader.entity

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

  fun getRequiredProps(props: Properties, ignoreGenerated: Boolean = false): Set<String> {
    val allProps = getAllProps()
    val existedProps = props.keys as Set<String>

    val allRequiredProps = allProps.minus(existedProps)
    return if (ignoreGenerated) {
      allRequiredProps
        .filterNot {
          GENERATED_PROP_MATCHER.containsMatchIn(it)
              || it.contains(PROPS_SIMPLE_NAME(""))
              || it.contains(PROPS_CLASS_NAME(""))
        }
        .toSet()
    } else {
      allRequiredProps
        .toSet()
    }
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

private fun String.getReferences(): Set<String> {
  val names = HashSet<String>()
  val velocityContext = VelocityContext()
  val ec = EventCartridge()
  ec.addEventHandler(ReferenceInsertionEventHandler { reference, _ -> names.add(reference) })
  ec.attachToContext(velocityContext)
  Velocity.evaluate(velocityContext, NullWriter.NULL_WRITER, "", this)
  return names
}
