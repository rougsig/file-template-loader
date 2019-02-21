package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROPS_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_INITIAL_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.github.rougsig.filetemplateloader.extension.createDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.extension.toPackageCase
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

class SingleFileTemplateCreator(
  private val template: FileTemplateSingle
) : FileTemplateCreator {
  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val relativePath = mergeTemplate(template.directory ?: "", props)

    val packageName = generatePackageName(props, relativePath)
    props.setProperty(PROPS_PACKAGE_NAME, packageName)

    val fileName = generateFileName(props, template)
    props.setProperty(PROPS_FILE_NAME, fileName)

    val targetDirectory = dir.createDirectoriesByRelativePath(relativePath)
    val fileNameWithExtension = generateFileNameWithExtension(props, template)

    val mergedTemplate = mergeTemplate(template.text, props)
    val file = targetDirectory.project.createPsiFile(fileNameWithExtension, mergedTemplate)
    targetDirectory.add(file)

    return listOf(file)
  }
}

fun generatePackageName(props: Props, relativePath: String): String {
  val packageNameBuilder = StringBuilder()
  packageNameBuilder.append(props.getProperty(PROPS_INITIAL_PACKAGE_NAME))
  if (relativePath.isNotBlank()) {
    packageNameBuilder.append(".")
    packageNameBuilder.append(relativePath.toPackageCase())
  }

  return packageNameBuilder.toString()
}

fun generateFileName(props: Props, template: FileTemplateSingle): String {
  val fileNameTemplate = template.fileName ?: props.getProperty(PROPS_FILE_NAME)
  return mergeTemplate(fileNameTemplate, props)
}

fun generateFileNameWithExtension(props: Props, template: FileTemplateSingle): String {
  return "${generateFileName(props, template)}.${template.extension}"
}
