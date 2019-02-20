package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.*
import java.util.*

typealias Props = Properties
typealias PropGenerators = Map<String, (String) -> String>

val PROP_GENERATORS: PropGenerators = HashMap<String, (String) -> String>().apply {
  put("LOWER_CAMEL_CASE", String::toLowerCamelCase)
  put("UPPER_CAMEL_CASE", String::toUpperCamelCase)
  put("LOWER_SNAKE_CASE", String::toLowerSnakeCase)
  put("UPPER_SNAKE_CASE", String::toUpperSnakeCase)
  put("LOWER_KEBAB_CASE", String::toLowerKebabCase)
  put("UPPER_KEBAB_CASE", String::toUpperKebabCase)
  put("PACKAGE_CASE", String::toPackageCase)
  put("SOLID_CASE", String::toSolidCase)
  put("UPPER_CASE", String::toUpperCase)
  put("LOWER_CASE", String::toLowerCase)
}

val GENERATED_PROP_MATCHER = PROP_GENERATORS.keys.joinToString("|") { it }.toRegex()

val PROPS_SIMPLE_NAME = { it: String -> "${it.toUpperSnakeCase()}_SIMPLE_NAME" }
val PROPS_CLASS_NAME = { it: String -> "${it.toUpperSnakeCase()}_CLASS_NAME" }
