package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirs
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateFolder(
  val pathName: String?,
  val templates: List<FileTemplateSingle>,
  override val name: String = ""
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateFolder: \n name: $name \n dir: $dir \n props: $props \n")

    val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)
    val folder = pathName?.let { dir.createSubDirs(it) } ?: dir

    return templates.flatMap { template ->
      props.setProperty(PROPS_PACKAGE_NAME, initialPackageName)
      template.create(folder, props)
    }
  }

  override fun getAllProps(): Set<String> {
    val templateProps = templates
      .flatMap { it.getAllProps() }
      .toTypedArray()

    val pathNameProps = pathName?.let { getTemplateProps(it).toTypedArray() } ?: emptyArray()

    return setOf(*templateProps, *pathNameProps)
  }

  override fun generateProps(props: Properties) {
    generateProps(props, templates) { getRequiredProps(it) }
    generateClassNameProps(props, templates, props.getProperty(PROPS_PACKAGE_NAME))
  }

  override val hasClassName: Boolean = false
}
