package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplate>,
  val directory: String,
  override val customProps: List<FileTemplateProp>
) : FileTemplate() {
  override val requiredProps = templates
    .flatMap(FileTemplate::requiredProps)
    .toSet()
    .plus(customProps.flatMap(FileTemplateProp::requiredProps))

  override fun generateProps(dir: PsiDirectory, props: Props) {
    props.setProperty("${simpleName}_$PROP_GROUP_NAME", name)
    super.generateProps(dir, props)
    templates.forEach { it.generateProps(dir, props) }
  }

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val subDir = dir.createSubDirectoriesByRelativePath(directory)
    return templates.flatMap { it.create(subDir, props) }
  }
}
