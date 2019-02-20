package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplateSingle>,
  val entries: List<FileTemplateEntry>
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateGroup: \n name: $name \n dir: $dir \n props: $props \n")

    val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)

    val createdTemplates = templates.flatMap { template ->
      props.setProperty(PROPS_PACKAGE_NAME, initialPackageName)
      template.create(dir, props)
    }

    val createdEntries = entries.flatMap { entry ->
      props.setProperty(PROPS_PACKAGE_NAME, initialPackageName)
      entry.create(dir, props)
    }

    return createdTemplates.plus(createdEntries)
  }

  override fun getAllProps(): Set<String> {
    val templateProps = templates
      .flatMap { it.getAllProps() }
      .toTypedArray()

    val entryProps = entries
      .flatMap { it.getAllProps() }
      .toTypedArray()

    val nameProps = getTemplateProps(name).toTypedArray()

    return setOf(*templateProps, *entryProps, *nameProps)
  }

  override fun generateProps(props: Properties) {
    generateProps(props, templates) { getRequiredProps(it) }
    generateClassNameProps(props, templates, props.getProperty(PROPS_PACKAGE_NAME))
  }

  override fun getRequiredProps(props: Properties, ignoreGenerated: Boolean): Set<String> {
    return super.getRequiredProps(props, ignoreGenerated)
      .filter { it != PROPS_NAME }
      .toSet()
  }

  override val hasClassName: Boolean = false
}
