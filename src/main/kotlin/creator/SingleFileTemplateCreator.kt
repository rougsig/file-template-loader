package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROPS_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.github.rougsig.filetemplateloader.extension.createDirectoriesByRelativePath
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateFileName
import com.github.rougsig.filetemplateloader.generator.generateFileNameWithExtension
import com.github.rougsig.filetemplateloader.generator.generatePackageName
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

fun createFileTemplate(dir: PsiDirectory, props: Props, template: FileTemplateSingle): PsiFile {
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

  return file
}
