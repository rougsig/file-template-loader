package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplate>,
  override val directory: String = "",
  override val customProps: List<FileTemplateCustomProp> = emptyList()
) : FileTemplate() {
  override val requiredProps = templates
    .requiredProps()
    .plusCustomProps()
    .minusGeneratedProps()

  override val initialGeneratedProps = setOf(
    "${simpleName}_$PROP_GROUP_NAME"
  ).plus(templates.flatMap(FileTemplate::generatedProps))

  override fun generateProps(dir: PsiDirectory, props: Props) {
    props.setProperty("${simpleName}_$PROP_GROUP_NAME", name)
    super.generateProps(dir, props)
    templates.forEach { it.generateProps(dir, props) }
  }

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val subDir = dir.createSubDirectoriesByRelativePath(directory)
    return templates.flatMap { it.create(subDir, props) }
  }

  private fun Set<String>.minusGeneratedProps(): Set<String> {
    return minus(templates.flatMap(FileTemplate::generatedProps))
  }
}
