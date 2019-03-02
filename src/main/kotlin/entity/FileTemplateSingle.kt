package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_DIRECTORY
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_NAME
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateSingle(
  override val name: String,
  private val text: String,
  private val directory: String,
  private val initialCustomProps: Set<FileTemplateCustomProp> = emptySet()
) : FileTemplate() {
  override val customProps: Set<FileTemplateCustomProp> = {
    val props = mutableSetOf<FileTemplateCustomProp>()

    props.add(FileTemplateCustomProp(PROP_TEMPLATE_NAME, name))
    if (directory.isNotBlank()) props.add(FileTemplateCustomProp(PROP_TEMPLATE_DIRECTORY, directory))
    props.addAll(initialCustomProps)

    props
  }()

  override val extractedProps: Set<String> =
    extractProps(text)
      .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))

  override val propGenerators: List<PropGenerator> =
    customProps
      .map { CustomPropGenerator(simpleName, customPropNames, it) }
      .plus(
        extractedProps.extractModificatorPropGenerators(
          simpleName,
          customPropNames
        )
      )

  override val requiredProps: Set<String> =
    extractedProps
      .plus(PROP_FILE_NAME)
      .minusGeneratedProps(simpleName, propGenerators)

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val localScopeProps = copyPropsToLocalScopeProps(simpleName, generatedProps, props)
    val mergedTemplate = mergeTemplate(text, localScopeProps)
    val fileName = localScopeProps.getProperty(PROP_FILE_NAME)
    val file = dir.createSubDirectoriesByRelativePath(directory)
      .add(dir.project.createPsiFile(fileName, mergedTemplate))
      .containingFile
    return listOf(file)
  }
}
