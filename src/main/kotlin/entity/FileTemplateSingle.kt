package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_NAME
import com.github.rougsig.filetemplateloader.creator.createSingleFileTemplate
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.generator.generatePackageName
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

  override val initialGeneratedProps = setOf(
    "${simpleName}_$PROP_TEMPLATE_NAME",
    "${simpleName}_$PROP_PACKAGE_NAME"
  )

  override fun generateProps(dir: PsiDirectory, props: Props) {
    props.setProperty("${simpleName}_$PROP_TEMPLATE_NAME", name)
    props.setProperty("${simpleName}_$PROP_PACKAGE_NAME", generatePackageName(props, directory))
    super.generateProps(dir, props)
  }

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val file = createSingleFileTemplate(dir.project, props, this)
    dir.createSubDirectoriesByRelativePath(directory).add(file)
    return listOf(file)
  }
}
