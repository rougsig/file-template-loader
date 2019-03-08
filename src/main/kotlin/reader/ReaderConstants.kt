package com.github.rougsig.filetemplateloader.reader

const val CONFIG_FILE_NAME = "config.properties"
const val FILE_TEMPLATE_FOLDER_NAME = ".fileTemplates"

const val FT_EXTENSION = ".ft"
const val JSON_EXTENSION = ".json"

const val FILE_TEMPLATE_MIXIN_EXTENSION = ".mixin.json"

val TEMPLATE_EXTENSIONS = listOf(
  FT_EXTENSION,
  JSON_EXTENSION
)

val TEMPLATE_EXTENSION_MATCHER = TEMPLATE_EXTENSIONS.joinToString("|") { it }.toRegex()
