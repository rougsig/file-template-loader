package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.generator.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplate>,
  override val customProps: Set<FileTemplateCustomProp> = emptySet()
) : FileTemplate() {
  override val extractedProps: Set<String> = templates
    .requiredProps()
    .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))

  private val initialPropGenerators = listOf(
    InitialPropGenerator(
      propName = "${simpleName}_$PROP_GROUP_NAME"
    ) { name }
  )

  private val initialPropNames = initialPropGenerators.map(PropGenerator::propName).toSet()
  private val customPropNames = customProps.map(FileTemplateCustomProp::name).toSet()

  override val propGenerators: List<PropGenerator> = initialPropGenerators
    .plus(customProps.map { CustomPropGenerator(simpleName, customPropNames, it) })
    .plus(templates.flatMap(FileTemplate::propGenerators))
    .plus(
      extractedProps.extractModificatorPropGenerators(
        simpleName,
        customPropNames,
        initialPropNames
      )
    )

  override val requiredProps: Set<String> = extractedProps
    .minusGeneratedProps(simpleName, propGenerators)

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    return templates.flatMap { it.create(dir, props) }
  }

  private fun List<FileTemplate>.requiredProps(): Set<String> {
    return flatMap { it.requiredProps }.toSet()
  }
}
