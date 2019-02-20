package com.github.rougsig.filetemplateloader.entity

data class FileTemplateGroupJson(
  val name: String,
  val templates: List<FileTemplateJson>,
  val entries: List<FileTemplateEntryJson>?
)

data class FileTemplateModuleJson(
  val name: String,
  val moduleName: String,
  val folders: List<FileTemplateFolderJson>,
  val entries: List<FileTemplateEntryJson>?
)

data class FileTemplateFolderJson(
  val pathName: String,
  val templates: List<FileTemplateJson>
)

data class FileTemplateJson(
  val template: String,
  val fileName: String,
  val directory: String?
)

data class FileTemplateEntryJson(
  val text: String,
  val className: String?,
  val pathName: String?,
  val selector: String
)