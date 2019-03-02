package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.generator.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateGroup(
  override val name: String,
  val templates: List<FileTemplate>,
  private val initialCustomProps: Set<FileTemplateCustomProp> = emptySet()
) : FileTemplate() {
  override val customProps: Set<FileTemplateCustomProp> = {
    val props = mutableSetOf<FileTemplateCustomProp>()

    props.add(FileTemplateCustomProp(PROP_GROUP_NAME, name))
    props.addAll(initialCustomProps)

    props
  }()

  override val extractedProps: Set<String> =
    templates
      .requiredProps()
      .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))

  override val propGenerators: List<PropGenerator> =
    customProps.map { CustomPropGenerator(simpleName, customPropNames, it) }
      .plus(templates.flatMap(FileTemplate::propGenerators))
      .plus(
        extractedProps.extractModificatorPropGenerators(
          simpleName,
          customPropNames
        )
      )

  override val requiredProps: Set<String> =
    extractedProps
      .minusGeneratedProps(simpleName, propGenerators)

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    return templates.flatMap { it.create(dir, props) }
  }

  private fun List<FileTemplate>.requiredProps(): Set<String> {
    return flatMap { it.requiredProps }.toSet()
  }
}
