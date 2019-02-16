package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirs
import com.github.rougsig.filetemplateloader.extension.getPackageNameWithSubDirs
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplateSingle>
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateGroup: \n name: $name \n dir: $dir \n props: $props \n")

    val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)

    return templates.flatMap { template ->
      val packageName = mergeTemplate(template.getPackageNameWithSubDirs(initialPackageName), props)
      val fileName = mergeTemplate(template.fileName!!, props)
      props.setProperty(PROPS_NAME, fileName)
      props.setProperty(PROPS_PACKAGE_NAME, packageName)

      val subDirs = try {
        template.createSubDirs(dir)
      } catch (e: Exception) {
        e.printStackTrace()
        throw e
      }
      template.create(subDirs, props)
    }
  }

  override fun getAllProps(): Set<String> {
    return templates
      .flatMap { it.getAllProps() }
      .toSet()
  }

  override fun getRequiredProps(props: Properties, ignoreGenerated: Boolean): Set<String> {
    return templates
      .flatMap { it.getRequiredProps(props, ignoreGenerated) }
      .filter { it != PROPS_NAME }
      .toSet()
  }

  override fun generateProps(props: Properties) {
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

    val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)

    templates.forEach { template ->
      val packageName = mergeTemplate(template.getPackageNameWithSubDirs(initialPackageName), props)
      val fileName = mergeTemplate(template.fileName!!, props)
      if (template.isSourceCode) props.setProperty(
        PROPS_CLASS_NAME(template.name),
        mergeTemplate("$packageName.$fileName", props)
      )
    }
  }

  override val isSourceCode: Boolean = false
}
