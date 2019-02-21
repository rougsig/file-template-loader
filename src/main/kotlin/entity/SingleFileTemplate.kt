package com.github.rougsig.filetemplateloader.entity

data class SingleFileTemplate(
  val name: String,
  val extension: String,
  val text: String
) {
  val extractedProps = extractProps(text)
}
