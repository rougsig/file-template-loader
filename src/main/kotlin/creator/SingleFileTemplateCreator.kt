package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.entity.SingleFileTemplate
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.github.rougsig.filetemplateloader.extension.createDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateFileName
import com.github.rougsig.filetemplateloader.generator.generateFileNameWithExtension
import com.github.rougsig.filetemplateloader.generator.generatePackageName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

fun createFileTemplate(dir: PsiDirectory, props: Props, template: FileTemplateSingle): PsiFile {
  val relativePath = mergeTemplate(template.directory ?: "", props)

  val packageName = generatePackageName(props, relativePath)
  props.setProperty(PROP_PACKAGE_NAME, packageName)

  val fileName = generateFileName(props, template)
  props.setProperty(PROP_FILE_NAME, fileName)

  val targetDirectory = dir.createDirectoriesByRelativePath(relativePath)
  val fileNameWithExtension = generateFileNameWithExtension(props, template)

  val mergedTemplate = mergeTemplate(template.text, props)
  val file = targetDirectory.project.createPsiFile(fileNameWithExtension, mergedTemplate)
  targetDirectory.add(file)

  return file
}

fun createSingleFileTemplate(project: Project, props: Props, template: SingleFileTemplate): PsiFile {
  val mergedTemplate = mergeTemplate(template.text, props)
  val fileName = props.getProperty(PROP_FILE_NAME) ?: ""
  val extension = template.extension
  return project.createPsiFile("$fileName.$extension", mergedTemplate)
}
