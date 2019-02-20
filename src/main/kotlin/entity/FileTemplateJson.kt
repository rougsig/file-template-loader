package com.github.rougsig.filetemplateloader.entity

data class FileTemplateGroupJson(
  val name: String,
  val templates: List<FileTemplateJson>,
  val injectors: List<FileTemplateInjectorJson>?
)

data class FileTemplateModuleJson(
  val name: String,
  val moduleName: String,
  val folders: List<FileTemplateFolderJson>,
  val injectors: List<FileTemplateInjectorJson>?
)

data class FileTemplateFolderJson(
  val pathName: String?,
  val templates: List<FileTemplateJson>
)

data class FileTemplateJson(
  val template: String,
  val fileName: String,
  val directory: String?
)

data class FileTemplateInjectorJson(
  val text: String,
  val className: String?,
  val pathName: String?,
  val selector: String
)
