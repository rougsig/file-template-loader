package com.github.rougsig.filetemplateloader.extension

fun String.beginWithUpperCase(): String {
  return when (length) {
    0 -> ""
    1 -> toUpperCase()
    else -> first().toUpperCase() + substring(1)
  }
}

fun String.toCamelCase(): String {
  return split('_').joinToString("") {
    it.beginWithUpperCase()
  }
}

fun String.toSnakeLowerCase(): String {
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

fun String.toSnakeUpperCase(): String {
  return toSnakeLowerCase().toUpperCase()
}

fun String.toPackageCase(): String {
  return toCamelCase().toSnakeLowerCase()
    .split('-', '_', '/', '\\')
    .joinToString(".") { it }
}
