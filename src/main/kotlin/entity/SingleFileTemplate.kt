package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_EXTENSION
import com.github.rougsig.filetemplateloader.constant.PROP_TEMPLATE_NAME
import com.github.rougsig.filetemplateloader.creator.createSingleFileTemplate
import com.github.rougsig.filetemplateloader.extension.createSubDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.generator.generatePackageName
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class SingleFileTemplate(
  override val name: String,
  val extension: String,
  val text: String,
  val directory: String,
  override val props: List<FileTemplateProp>
) : FileTemplate() {
  override val requiredProps = extractProps(text)
    .plus(props.flatMap(FileTemplateProp::requiredProps))

  override fun generateProps(props: Props) {
    props.setProperty(PROP_TEMPLATE_NAME, name)
    props.setProperty(PROP_TEMPLATE_EXTENSION, extension)
    generatePackageName(props, directory)
    super.generateProps(props)
  }

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val file = createSingleFileTemplate(dir.project, props, this)
    dir.createSubDirectoriesByRelativePath(directory).add(file)
    return listOf(file)
  }
}
