package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateSingle(
  override val name: String,
  val extension: String,
  val text: String,
  val fileName: String? = null,
  val directory: String? = null
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateSingle: \n name: $name \n dir: $dir \n props: $props \n")

    val fileName = props.getProperty(PROPS_NAME)
    val fileNameWithExtension = "$fileName.$extension"
    dir.checkCreateFile(fileNameWithExtension)

    val templateResult = mergeTemplate(text, props)
    val file = dir.add(dir.project.createPsiFile(fileNameWithExtension, templateResult)) as PsiFile

    return listOf(file)
  }

  override fun getAllProps(): Set<String> {
    val templateProps = getTemplateProps(text)
    return fileName
      ?.let { templateProps.plus(getTemplateProps(it)) }
      ?: templateProps
  }

  override fun generateProps(props: Properties) {
    generateProps(getRequiredProps(props), props)
  }

  override val hasClassName: Boolean = extension == "kt"
}
