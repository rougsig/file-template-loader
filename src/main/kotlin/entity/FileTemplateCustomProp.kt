package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.extractProps

data class FileTemplateCustomProp(
  val name: String,
  val text: String
) {
  val requiredProps = extractProps("$name\n$text")
}
