package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_DIRECTORY
import com.github.rougsig.filetemplateloader.constant.PROP_GROUP_NAME
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.*
import com.github.rougsig.filetemplateloader.scope.PropScope
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateGroup(
  override val fullName: String,
  val templates: List<ScopedFileTemplate>,
  val injectors: List<FileTemplateInjector> = emptyList(),
  private val initialCustomProps: Set<FileTemplateCustomProp> = emptySet()
) : ScopedFileTemplate() {
  override val customProps: Set<FileTemplateCustomProp> = {
    val props = mutableSetOf<FileTemplateCustomProp>()

    props.add(FileTemplateCustomProp(PROP_GROUP_NAME, name))
    props.addAll(initialCustomProps)

    if (initialCustomProps.find { it.name == PROP_FILE_DIRECTORY } == null)
      props.add(FileTemplateCustomProp(PROP_FILE_DIRECTORY, ""))

    props
  }()

  override val extractedProps: Set<String> =
    templates
      .requiredProps()
      .plus(customProps.flatMap(FileTemplateCustomProp::requiredProps))
      .plus(injectors.flatMap(FileTemplateInjector::requiredProps))

  override val scope: PropScope = PropScope(
    name = simpleName,
    childScopes = templates.mapTo(HashSet(), ScopedFileTemplate::scope),
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
    val directory = localScopeProps.requireProperty(PROP_FILE_DIRECTORY)
    println("Create GROUP: `$name` TO: `$directory`")

    val subDirectory = dir.createSubDirectoriesByRelativePath(directory)

    val createdTemplates = templates.flatMap { it.create(subDirectory, localScopeProps) }
    val injectedFiles = injectors.flatMap { it.create(dir, localScopeProps) }

    return createdTemplates.plus(injectedFiles)
  }

  private fun List<FileTemplate>.requiredProps(): Set<String> {
    return flatMap { it.requiredProps }.toSet()
  }
}
