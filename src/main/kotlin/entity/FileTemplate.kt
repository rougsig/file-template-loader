package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

abstract class FileTemplate {
  abstract val name: String
  abstract val requiredProps: Set<String>
  abstract val props: List<FileTemplateProp>

  abstract fun create(dir: PsiDirectory, props: Props): List<PsiFile>

  open fun generateProps(dir: PsiDirectory, props: Props) {
    this.props.forEach { prop ->
      val merged = mergeTemplate(prop.text, props)
      props.setProperty(
        "${this.name.toUpperSnakeCase()}_${prop.name}",
        merged
      )
      props.setProperty(
        prop.name,
        merged
      )
    }
    requiredProps.generateProps(props)
  }
}

fun mergeTemplate(templateText: String, props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, templateText, true)
  return StringUtil.convertLineSeparators(merged)
}

