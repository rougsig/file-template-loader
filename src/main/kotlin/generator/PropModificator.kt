package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.extension.*
import java.util.*

typealias PropModificators = Map<String, (String) -> String>

val PROP_MODIFICATORS: PropModificators = HashMap<String, (String) -> String>().apply {
  put("LOWER_CAMEL_CASE", String::toLowerCamelCase)
  put("UPPER_CAMEL_CASE", String::toUpperCamelCase)
  put("LOWER_SNAKE_CASE", String::toLowerSnakeCase)
  put("UPPER_SNAKE_CASE", String::toUpperSnakeCase)
  put("LOWER_KEBAB_CASE", String::toLowerKebabCase)
  put("UPPER_KEBAB_CASE", String::toUpperKebabCase)
  put("DOT_CASE", String::toDotCase)
  put("SLASH_CASE", String::toSlashCase)
  put("SOLID_CASE", String::toSolidCase)
  put("UPPER_CASE", String::toUpperCase)
  put("LOWER_CASE", String::toLowerCase)
  put("APPEND_DOT_CASE", String::appendDotCase)
}

val PROP_MODIFICATOR_MATCHER = PROP_MODIFICATORS.keys.joinToString("|") { "_$it" }.toRegex()
