package com.github.rougsig.filetemplateloader.entity

data class FileTemplate(
  val name: String,
  val fileName: String,
  val extension: String,
  val text: String,
  val directory: String? = null
)
