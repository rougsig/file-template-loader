package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class GroupFileTemplate(
  override val name: String,
  val templates: List<FileTemplate>,
  val directory: String,
  override val props: List<FileTemplateProp>
) : FileTemplate() {
  override val requiredProps = templates
    .flatMap(FileTemplate::requiredProps)
    .toSet()
    .plus(props.flatMap(FileTemplateProp::requiredProps))

  override fun generateProps(props: Props) {
    props.setProperty(PROP_GROUP_NAME, name)
    super.generateProps(props)
    templates.forEach { it.generateProps(props) }
  }

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val subDir = dir.createSubDirectoriesByRelativePath(directory)
    return templates.flatMap { it.create(subDir, props) }
  }
}
