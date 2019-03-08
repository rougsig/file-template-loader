package com.github.rougsig.filetemplateloader.entity

data class FileTemplateMixin(
  val name: String,
  val pattern: Regex?,
  val customProps: Set<FileTemplateCustomProp>
) {
  override fun equals(other: Any?): Boolean {
    return other == name
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
