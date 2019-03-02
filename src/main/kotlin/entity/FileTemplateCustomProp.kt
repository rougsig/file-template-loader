package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.extractProps

data class FileTemplateCustomProp(
  val name: String,
  @Transient val text: String
) {
  val requiredProps = extractProps("$name\n$text")

  override fun equals(other: Any?): Boolean {
    return name.equals(other)
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
