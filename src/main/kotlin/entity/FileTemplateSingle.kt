package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.createDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.extension.getPackageNameWithSubDirs
import com.github.rougsig.filetemplateloader.generator.extractProps
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
      dir.createDirectoriesByRelativePath(mergeTemplate(directory, props))
    } else {
      dir
    }
    val packageName = mergeTemplate(getPackageNameWithSubDirs(props.getProperty(PROP_PACKAGE_NAME)), props)
    if (fileName != null) props.setProperty(PROP_FILE_NAME, mergeTemplate(fileName, props))
    props.setProperty(PROP_PACKAGE_NAME, packageName)

    println("Create FileTemplateSingle: \n name: $name \n dir: $subDir \n extractedProps: $props \n")

    val fileName = props.getProperty(PROP_FILE_NAME)
    val fileNameWithExtension = "$fileName.$extension"
    subDir.checkCreateFile(fileNameWithExtension)

    val templateResult = mergeTemplate(text, props)
    val file = subDir.add(subDir.project.createPsiFile(fileNameWithExtension, templateResult)) as PsiFile

    return listOf(file)
  }

  override fun getAllProps(): Set<String> {
    val templateProps = extractProps(text)
    return fileName
      ?.let { templateProps.plus(extractProps(it)) }
      ?: templateProps
  }

  override fun generateProps(props: Properties) {
    generateProps(getRequiredProps(props), props)
  }

  override fun getRequiredProps(props: Properties): Set<String> {
    return super.getRequiredProps(props).plus(PROP_FILE_NAME).toSet()
  }

  override val hasClassName: Boolean = extension == "kt"
}
