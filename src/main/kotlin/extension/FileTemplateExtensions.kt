package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.PropGenerator
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.parents
import org.apache.commons.io.output.NullWriter
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.event.EventCartridge
import org.apache.velocity.app.event.ReferenceInsertionEventHandler
import java.util.*

fun String.mergeTemplate(props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, this, true)
  return StringUtil.convertLineSeparators(merged)
}

// FIXME Move "./" logic to separate function
fun PsiDirectory.createSubDirs(directory: String): PsiDirectory {
  val startDirectory = if (directory.startsWith("./")) {
    parents().last() as PsiDirectory
  } else {
    this
  }
  val subDirs = directory.replace("./", "").split("/")
  return subDirs.fold(startDirectory) { acc, dirName ->
    acc.findSubdirectory(dirName) ?: acc.createSubdirectory(dirName)
  }
}

fun FileTemplate.mergeTemplate(props: Properties): String {
  return text.mergeTemplate(props)
}

fun FileTemplate.mergeFileName(props: Properties): String {
  return fileName.mergeTemplate(props)
}

fun FileTemplate.createSubDirs(initialDir: PsiDirectory): PsiDirectory {
  return directory?.let { initialDir.createSubDirs(it) } ?: initialDir
}

fun FileTemplate.getPackageNameWithSubDirs(initialPackageName: String): String {
  val subDirs = directory ?: return initialPackageName
  return initialPackageName + "." + subDirs.toPackageCase()
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

fun FileTemplate.getAllProps(): List<String> {
  return text.getReferences()
    .plus(fileName.getReferences())
    .map {
      it.replace("{", "")
        .replace("}", "")
        .replace("\$", "")
    }
}

fun FileTemplate.getGeneratedProps(generators: List<PropGenerator>): Set<String> {
  val generatorPostfixes = generators.map(PropGenerator::name)
  return getAllProps()
    .filter { prop -> generatorPostfixes.any { prop.endsWith(it) } }
    .toSet()
}

fun FileTemplate.getGeneratedPropsBase(generators: List<PropGenerator>): Set<String> {
  val generatorPostfixes = generators.map(PropGenerator::name)
  val matcher = generatorPostfixes.joinToString("|_") { it }.toRegex()
  return getGeneratedProps(generators)
    .map { prop -> matcher.replace(prop) { "" } }
    .toSet()
}

fun FileTemplate.getProps(generators: List<PropGenerator>): Set<String> {
  return getAllProps()
    .minus(getGeneratedProps(generators))
    .plus(getGeneratedPropsBase(generators))
    .toSet()
}

fun List<PropGenerator>.generateProps(props: Properties, propBases: Set<String>) {
  propBases
    .forEach { prop ->
      println("Generate props for: $prop")
      forEach { generator ->
        props.setProperty(prop + "_" + generator.name, generator.generate(props.getProperty(prop).mergeTemplate(props)))
      }
    }
}

