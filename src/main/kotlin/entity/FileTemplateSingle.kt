package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_NAME
import com.github.rougsig.filetemplateloader.creator.createSingleFileTemplate
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateSingle(
  override val name: String,
  val text: String,
  override val directory: String = "",
  override val customProps: List<FileTemplateCustomProp> = emptyList()
) : FileTemplate() {
  override val extractedProps: Set<String> = extractProps(text)
    .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))

  private val initialPropGenerators = listOf(
    InitialPropGenerator(
      propName = "${simpleName}_$PROP_TEMPLATE_NAME"
    ) { name },
    PackageNamePropGenerator(
      prefix = simpleName,
      directory = directory
    )
  )

  private val initialPropNames = initialPropGenerators.map(PropGenerator::propName).toSet()
  private val customPropNames = customProps.map(FileTemplateCustomProp::name).toSet()

  override val propGenerators: List<PropGenerator> =
    initialPropGenerators
      .plus(customProps.map { CustomPropGenerator(simpleName, it) })
      .plus(
        extractedProps.extractModificatorPropGenerators(
          simpleName,
          customPropNames,
          initialPropNames
        )
      )

  override val requiredProps: Set<String> = extractedProps
    .plus(PROP_FILE_NAME)
    .minusGeneratedProps(simpleName, propGenerators)

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val file = createSingleFileTemplate(dir.project, props, this)
    dir.createSubDirectoriesByRelativePath(directory).add(file)
    return listOf(file)
  }
}
