package com.github.rougsig.filetemplateloader

import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.util.projectStructure.module
import java.util.*

fun FileTemplate.create(dir: PsiDirectory, props: Properties): PsiElement {
  if (props.getProperty(PROPS_PACKAGE_NAME).isNullOrBlank()) {
    props.setProperty(PROPS_PACKAGE_NAME, dir.calculatePackageName(props))
  }

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

fun PsiDirectory.calculatePackageName(props: Properties): String {
  val builder = StringBuilder()
  getKotlinFqName()
    ?.let { builder.append(it.toString()) }
    ?: { builder.append(props.getProperty(PACKAGE_BASE)) }()

  module?.name?.toPackageCase()?.let {
    builder.append(".")
    builder.append(it)
  }

  return builder.toString()
}