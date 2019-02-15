package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROPS_CLASS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_SIMPLE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.extension.mergeTemplate
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import java.util.*

fun FileTemplate.create(dir: PsiDirectory, props: Properties): PsiElement {
  val templateName = props.getProperty(PROPS_NAME)
  val packageName = props.getProperty(PROPS_PACKAGE_NAME)

  val fileName = "$templateName.$extension"

  props.setProperty(PROPS_SIMPLE_NAME(name), templateName)
  props.setProperty(PROPS_CLASS_NAME(name), "$packageName.$templateName")

  dir.checkCreateFile(fileName)

  val project = dir.project
  val template = text.mergeTemplate(props)
  return dir.add(project.createPsiFile(fileName, template)) as PsiFile
}
