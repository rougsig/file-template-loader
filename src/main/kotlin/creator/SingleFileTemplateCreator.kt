package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.entity.SingleFileTemplate
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.github.rougsig.filetemplateloader.extension.createPsiFile
import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

fun createSingleFileTemplate(project: Project, props: Props, template: SingleFileTemplate): PsiFile {
  val mergedTemplate = mergeTemplate(template.text, props)
  val fileName = props.getProperty(PROP_FILE_NAME) ?: ""
  val extension = template.extension
  return project.createPsiFile("$fileName.$extension", mergedTemplate)
}
