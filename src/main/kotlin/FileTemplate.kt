package com.github.rougsig.filetemplateloader

data class FileTemplate(
  val name: String,
  val extension: String,
  val text: String,
  val directory: String? = null
)
