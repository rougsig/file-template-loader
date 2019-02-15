package com.github.rougsig.filetemplateloader

const val PROPS_FILE_NAME = "FILE_NAME"
const val PROPS_PACKAGE_NAME = "PACKAGE_NAME"
val PROPS_SIMPLE_NAME = { it: String -> "${it.toSnakeUpperCase()}_SIMPLE_NAME" }
val PROPS_CLASS_NAME = { it: String -> "${it.toSnakeUpperCase()}_CLASS_NAME" }
