package com.github.rougsig.filetemplateloader

import com.intellij.ide.fileTemplates.impl.CustomFileTemplate

data class FileTemplate(
  val name: String,
  val extension: String,
  val text: String,
  val directory: String? = null
) {
  val template by lazy {
    CustomFileTemplate(
      name,
      extension
    ).apply { text = this@FileTemplate.text }
  }
}
