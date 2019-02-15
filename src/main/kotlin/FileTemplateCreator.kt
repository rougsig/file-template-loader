package com.github.rougsig.filetemplateloader

import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
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

fun String.mergeTemplate(props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, this, true)
  return StringUtil.convertLineSeparators(merged)
}

