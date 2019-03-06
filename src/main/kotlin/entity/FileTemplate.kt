package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.generator.filterProps
import com.github.rougsig.filetemplateloader.reader.FILE_NAME_DELIMITER
import com.github.rougsig.filetemplateloader.scope.PropScope
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

abstract class FileTemplate {
  abstract val name: String

  abstract val extractedProps: Set<String>
  abstract val requiredProps: Set<String>

  abstract val customProps: Set<FileTemplateCustomProp>

  abstract val scope: PropScope

  val simpleName by lazy(LazyThreadSafetyMode.NONE) {
    (name
      .split(FILE_NAME_DELIMITER)
      .find { it.isNotBlank() && it != FILE_NAME_DELIMITER } ?: name)
      .toUpperSnakeCase()
  }

  abstract fun create(dir: PsiDirectory, props: Props): List<PsiFile>
}

fun mergeTemplate(templateText: String, props: Props): String {
  val requiredProps = extractProps(templateText).filterProps(props)
  check(requiredProps.isEmpty()) {
    "cannot merge template props not found: ${requiredProps.joinToString { it }}"
  }

  val merged = FileTemplateUtil.mergeTemplate(props, templateText, true)
  return StringUtil.convertLineSeparators(merged)
}
