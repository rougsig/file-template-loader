package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_DIRECTORY
import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.*
import com.github.rougsig.filetemplateloader.scope.PropScope
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

  override val scope: PropScope = PropScope(
    name = simpleName,
    childScopes = templates.mapTo(HashSet(), FileTemplate::scope),
    propGenerators = customProps
      .map { CustomPropGenerator(it) }
      .plus(extractedProps.extractModificatorPropGenerators())
      .toSet()
  )

  override val requiredProps: Set<String> =
    extractedProps
      .applyPropGenerators(scope.propGenerators)
      .minus(scope.scopedPropGenerators.map(PropGenerator::propName))
      .minus(scope.childScopes.flatMap { it.scopedPropGenerators.map(PropGenerator::propName) })

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val localScopeProps = scope.copyPropsToLocalScope(props)
    val directory = localScopeProps.getProperty(PROP_FILE_DIRECTORY) ?: ""
    val subDirectory = dir.createSubDirectoriesByRelativePath(directory)

    return templates.flatMap { it.create(subDirectory, localScopeProps) }
  }

  private fun List<FileTemplate>.requiredProps(): Set<String> {
    return flatMap { it.requiredProps }.toSet()
  }
}
