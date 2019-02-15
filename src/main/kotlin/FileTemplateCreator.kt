package com.github.rougsig.filetemplateloader

import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import java.util.*

fun FileTemplate.create(dir: PsiDirectory, props: Properties): PsiElement {
  props.setProperty(PROPS_PACKAGE_NAME, dir.calculatePackageName())

  val fileName = props.getProperty(PROPS_FILE_NAME)
  val createdTemplate = FileTemplateUtil.createFromTemplate(
    template,
    fileName,
    props,
    dir
  )

  props.setProperty(PROPS_SIMPLE_NAME(name), fileName)
  props.setProperty(PROPS_CLASS_NAME(name), "${props.getProperty(PROPS_PACKAGE_NAME)}.$fileName")

  return createdTemplate
}

fun PsiDirectory.calculatePackageName(): String {
  return "com.github.rougsig.filetemplateloader"
}
