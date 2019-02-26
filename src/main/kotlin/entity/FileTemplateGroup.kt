package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.*
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

  private val initialPropGenerators = listOf(
    InitialPropGenerator(
      propName = "${simpleName}_$PROP_GROUP_NAME"
    ) { name }
  )

  override val propGenerators: List<PropGenerator> = initialPropGenerators
    .plus(customProps.map { CustomPropGenerator(simpleName, it) })
    .plus(templates.flatMap(FileTemplate::propGenerators))
    .plus(requiredProps.extractModificatorPropGenerators())

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val subDir = dir.createSubDirectoriesByRelativePath(directory)
    return templates.flatMap { it.create(subDir, props) }
  }

  private fun Set<String>.minusGeneratedProps(): Set<String> {
    return minus(templates.flatMap(FileTemplate::generatedProps))
  }

  private fun List<FileTemplate>.requiredProps(): Set<String> {
    return flatMap(FileTemplate::requiredProps).toSet()
  }
}
