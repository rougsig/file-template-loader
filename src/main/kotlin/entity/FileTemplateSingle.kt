package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_DIRECTORY
import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_EXTENSION
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_NAME
import com.github.rougsig.filetemplateloader.extension.createFileToDirectory
import com.github.rougsig.filetemplateloader.extension.mergeTemplate
import com.github.rougsig.filetemplateloader.generator.*
import com.github.rougsig.filetemplateloader.scope.PropScope
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateSingle(
  override val fullName: String,
  private val text: String,
  private val initialCustomProps: Set<FileTemplateCustomProp> = emptySet()
) : ScopedFileTemplate() {
  override val customProps: Set<FileTemplateCustomProp> = {
    val props = mutableSetOf<FileTemplateCustomProp>()

    props.add(FileTemplateCustomProp(PROP_TEMPLATE_NAME, name))
    props.add(FileTemplateCustomProp(PROP_TEMPLATE_EXTENSION, extension))
    props.addAll(initialCustomProps)

    if (initialCustomProps.find { it.name == PROP_FILE_DIRECTORY } == null)
      props.add(FileTemplateCustomProp(PROP_FILE_DIRECTORY, ""))

    props
  }()

  override val extractedProps: Set<String> =
    extractProps(text)
      .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))

  override val scope: PropScope = PropScope(
    name = simpleName,
    childScopes = emptySet(),
    propGenerators = customProps
      .map { CustomPropGenerator(it) }
      .plus(extractedProps.extractModificatorPropGenerators())
      .toSet()
  )

  override val requiredProps: Set<String> =
    extractedProps
      .plus(PROP_FILE_NAME)
      .applyPropGenerators(scope.propGenerators)
      .minus(scope.scopedPropGenerators.map(PropGenerator::propName))

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val localScopeProps = scope.copyPropsToLocalScope(props)
    val mergedTemplate = mergeTemplate(text, localScopeProps)

    val fileName = localScopeProps.requireProperty(PROP_FILE_NAME)
    val directory = localScopeProps.requireProperty(PROP_FILE_DIRECTORY)
    println("Create TEMPLATE=$name\nTO=$directory\nPROPS=$localScopeProps\n")

    return listOf(dir.createFileToDirectory(directory, fileName, mergedTemplate))
  }
}
