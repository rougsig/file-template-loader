package com.github.rougsig.filetemplateloader.entity

import com.google.gson.JsonObject

data class FileTemplateGroupJson(
  val name: String?,
  val templates: List<FileTemplateJson>,
  val variables: JsonObject?,
  val injectors: List<FileTemplateInjectorJson>?
)

data class FileTemplateJson(
  val name: String?,
  val content: String?,
  val contentFrom: String?,
  val variables: JsonObject?
)

data class FileTemplateInjectorJson(
  val text: String,
  val className: String?,
  val pathName: String?,
  val selector: String
)
