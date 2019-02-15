package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROPS_CLASS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_SIMPLE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.github.rougsig.filetemplateloader.extension.*
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

fun FileTemplate.create(dir: PsiDirectory, props: Properties): PsiFile {
  val templateName = props.getProperty(PROPS_NAME)
  val packageName = props.getProperty(PROPS_PACKAGE_NAME)

  val fileName = "$templateName.$extension"

  props.setProperty(PROPS_SIMPLE_NAME(name), templateName)
  props.setProperty(PROPS_CLASS_NAME(name), "$packageName.$templateName")

  dir.checkCreateFile(fileName)

  val project = dir.project
  val template = mergeTemplate(props)
  return dir.add(project.createPsiFile(fileName, template)) as PsiFile
}

fun FileTemplateGroup.create(dir: PsiDirectory, props: Properties): List<PsiFile> {
  val initialPackageName = props.getProperty(PROPS_PACKAGE_NAME)

  return templates.map {
    props.setProperty(PROPS_NAME, it.mergeFileName(props))
    props.setProperty(PROPS_PACKAGE_NAME, it.getPackageNameWithSubDirs(initialPackageName))
    it.create(it.createSubDirs(dir), props)
  }
}
