package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.generator.filterProps
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil

fun mergeTemplate(templateText: String, props: Props): String {
  val requiredProps = extractProps(templateText).filterProps(props)
  check(requiredProps.isEmpty()) {
    "cannot merge template props not found: ${requiredProps.joinToString { it }}"
  }

  val merged = FileTemplateUtil.mergeTemplate(props, templateText, true)
  return StringUtil.convertLineSeparators(merged)
}
