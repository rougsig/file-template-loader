package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.extension.createSubDirs
import com.github.rougsig.filetemplateloader.extension.getPackageNameWithSubDirs
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
    val subDir = if (!directory.isNullOrBlank()) {
      dir.createSubDirs(mergeTemplate(directory, props))
    } else {
      dir
    }
    val packageName = mergeTemplate(getPackageNameWithSubDirs(props.getProperty(PROPS_PACKAGE_NAME)), props)
    if (fileName != null) props.setProperty(PROPS_NAME, mergeTemplate(fileName, props))
    props.setProperty(PROPS_PACKAGE_NAME, packageName)

    println("Create FileTemplateSingle: \n name: $name \n dir: $subDir \n props: $props \n")

    val fileName = props.getProperty(PROPS_NAME)
    val fileNameWithExtension = "$fileName.$extension"
    subDir.checkCreateFile(fileNameWithExtension)

    val templateResult = mergeTemplate(text, props)
    val file = subDir.add(subDir.project.createPsiFile(fileNameWithExtension, templateResult)) as PsiFile

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
