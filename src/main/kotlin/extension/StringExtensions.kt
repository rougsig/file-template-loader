package com.github.rougsig.filetemplateloader.extension

fun String.beginWithUpperCase(): String {
  return when (length) {
    0 -> ""
    1 -> toUpperCase()
    else -> first().toUpperCase() + substring(1)
  }
}

fun String.beginWithLowerCase(): String {
  return when (length) {
    0 -> ""
    1 -> toLowerCase()
    else -> first().toLowerCase() + substring(1)
  }
}

fun String.toUpperCamelCase(): String {
  return split('_').joinToString("") {
    it.beginWithUpperCase()
  }
}

fun String.toLowerCamelCase(): String {
  return toUpperCamelCase().beginWithLowerCase()
}

fun String.toLowerSnakeCase(): String {
  val builder = StringBuilder()
  var isFirst = true
  forEach {
    if (it.isUpperCase()) {
      if (isFirst) isFirst = false
      else builder.append("_")
      builder.append(it.toLowerCase())
    } else {
      builder.append(it)
    }
  }
  return builder.toString()
}

fun String.toUpperSnakeCase(): String {
  return toLowerSnakeCase().toUpperCase()
}

fun String.toLowerKebabCase(): String {
  return toLowerSnakeCase().replace("_", "-")
}

fun String.toUpperKebabCase(): String {
  return toUpperSnakeCase().replace("_", "-")
}

fun String.toDotCase(): String {
  return toUpperCamelCase()
    .toLowerSnakeCase()
    .replace("./", "/")
    .split('-', '_', '/', '\\')
    .filter(String::isNotBlank)
    .joinToString(".") { it }
}

fun String.toSlashCase(): String {
  return toDotCase().replace(".", "/")
}

fun String.toSolidCase(): String {
  return toUpperCamelCase().toLowerSnakeCase()
    .replace("./", "/")
    .split('-', '_', '/', '\\')
    .filter(String::isNotBlank)
    .joinToString("") { it }
}

fun String.appendDotCase(): String {
  return ".${toDotCase()}".takeIf { isNotBlank() } ?: ""
}
