package com.github.rougsig.filetemplateloader.extension

import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import java.util.*

fun String.mergeTemplate(props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, this, true)
  return StringUtil.convertLineSeparators(merged)
}
