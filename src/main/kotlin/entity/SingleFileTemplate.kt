package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.extractProps

data class SingleFileTemplate(
  val name: String,
  val extension: String,
  val text: String
) {
  val extractedProps = extractProps(text)
}
