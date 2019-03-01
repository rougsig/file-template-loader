package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase
import com.github.rougsig.filetemplateloader.generator.PropGenerator
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.reader.FILE_NAME_DELIMITER
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

abstract class FileTemplate {
  abstract val name: String

  abstract val extractedProps: Set<String>
  abstract val requiredProps: Set<String>
  abstract val customProps: Set<FileTemplateCustomProp>

  abstract val propGenerators: List<PropGenerator>

  val generatedProps by lazy(LazyThreadSafetyMode.NONE) {
    propGenerators.map(PropGenerator::propName).toSet()
  }

  val simpleName by lazy(LazyThreadSafetyMode.NONE) {
    (name
      .split(FILE_NAME_DELIMITER)
      .find { it.isNotBlank() && it != FILE_NAME_DELIMITER } ?: name)
      .toUpperSnakeCase()
  }

  abstract fun create(dir: PsiDirectory, props: Props): List<PsiFile>
}

fun mergeTemplate(templateText: String, props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, templateText, true)
  return StringUtil.convertLineSeparators(merged)
}
