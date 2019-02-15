package com.github.rougsig.filetemplateloader.constant

import com.github.rougsig.filetemplateloader.extension.toSnakeUpperCase

const val PROPS_NAME = "NAME"
const val PROPS_PACKAGE_NAME = "PACKAGE_NAME"
const val PACKAGE_BASE = "PACKAGE_BASE"
const val PROJECT_NAME = "PROJECT_NAME"
val PROPS_SIMPLE_NAME = { it: String -> "${it.toSnakeUpperCase()}_SIMPLE_NAME" }
val PROPS_CLASS_NAME = { it: String -> "${it.toSnakeUpperCase()}_CLASS_NAME" }
