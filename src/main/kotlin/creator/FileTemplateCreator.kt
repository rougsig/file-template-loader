package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.github.rougsig.filetemplateloader.entity.generateProps
import com.github.rougsig.filetemplateloader.extension.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

fun FileTemplate.create(dir: PsiDirectory, props: Properties): PsiFile {
  props.generateProps(this)
  println("Create FileTemplate: \n name: $name \n dir: $dir \n props: $props \n")

  val fileName = props.getProperty(PROPS_NAME)
  val fileNameWithExtension = "$fileName.$extension"

  dir.checkCreateFile(fileNameWithExtension)

  val project = dir.project
  val template = mergeTemplate(props)
  return dir.add(project.createPsiFile(fileNameWithExtension, template)) as PsiFile
}

fun FileTemplateGroup.create(dir: PsiDirectory, props: Properties): List<PsiFile> {
  val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)

  return templates.map {
    props.setProperty(PROPS_NAME, it.mergeFileName(props))
    props.setProperty(PROPS_PACKAGE_NAME, it.getPackageNameWithSubDirs(initialPackageName))
    it.create(it.createSubDirs(dir), props)
  }
}
