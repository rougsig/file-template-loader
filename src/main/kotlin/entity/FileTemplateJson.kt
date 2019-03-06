package com.github.rougsig.filetemplateloader.entity

data class FileTemplateGroupJson(
  val name: String?,
  val templates: List<FileTemplateJson>,
  val customProps: List<FileTemplateCustomPropJson>?,
  val injectors: List<FileTemplateInjectorJson>?
)

data class FileTemplateJson(
  val name: String?,
  val text: String?,
  val textFrom: String?,
  val directory: String?,
  val customProps: List<FileTemplateCustomPropJson>?
)

data class FileTemplateCustomPropJson(
  val name: String,
  val text: String
)

data class FileTemplateInjectorJson(
  val text: String,
  val className: String?,
  val pathName: String?,
  val selector: String
)
