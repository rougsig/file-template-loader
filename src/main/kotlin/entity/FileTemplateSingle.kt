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
  override val requiredProps = extractProps(text)
    .plus(PROP_FILE_NAME)
    .plusCustomProps()

  private val initialPropGenerators = listOf(
    InitialPropGenerator(
      propName = "${simpleName}_$PROP_TEMPLATE_NAME"
    ) { name },
    PackageNamePropGenerator(
      prefix = simpleName,
      directory = directory
    )
  )

  override val propGenerators: List<PropGenerator> =
    initialPropGenerators
      .plus(customProps.map { CustomPropGenerator(simpleName, it) })
      .plus(requiredProps.extractModificatorPropGenerators())

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val file = createSingleFileTemplate(dir.project, props, this)
    dir.createSubDirectoriesByRelativePath(directory).add(file)
    return listOf(file)
  }
}
