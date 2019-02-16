package com.github.rougsig.filetemplateloader.constant

import com.github.rougsig.filetemplateloader.entity.PropGenerator
import com.github.rougsig.filetemplateloader.extension.toLowerCamelCase
import com.github.rougsig.filetemplateloader.extension.toLowerSnakeCase
import com.github.rougsig.filetemplateloader.extension.toUpperCamelCase
import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase

const val PROPS_NAME = "NAME"
const val PROPS_PACKAGE_NAME = "PACKAGE_NAME"
const val PACKAGE_BASE = "PACKAGE_BASE"
const val PROJECT_NAME = "PROJECT_NAME"

val PROPS_SIMPLE_NAME = { it: String -> "${it.toUpperSnakeCase()}_SIMPLE_NAME" }
val PROPS_CLASS_NAME = { it: String -> "${it.toUpperSnakeCase()}_CLASS_NAME" }
val PROPS_GENERATORS = listOf(
  PropGenerator(
    name = "LOWER_CAMEL_CASE",
    generator = String::toLowerCamelCase
  ),
  PropGenerator(
    name = "UPPER_CAMEL_CASE",
    generator = String::toUpperCamelCase
  ),
  PropGenerator(
    name = "LOWER_SHAKE_CASE",
    generator = String::toLowerSnakeCase
  ),
  PropGenerator(
    name = "UPPER_SNAKE_CASE",
    generator = String::toUpperSnakeCase
  )
)
