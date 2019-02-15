package com.github.rougsig.filetemplateloader

fun String.beginWithUpperCase(): String {
  return when (this.length) {
    0 -> ""
    1 -> this.toUpperCase()
    else -> this[0].toUpperCase() + this.substring(1)
  }
}

fun String.toCamelCase(): String {
  return this.split('_').map {
    it.beginWithUpperCase()
  }.joinToString("")
}

fun String.toSnakeCase(): String {
  var text: String = ""
  var isFirst = true
  this.forEach {
    if (it.isUpperCase()) {
      if (isFirst) isFirst = false
      else text += "_"
      text += it.toLowerCase()
    } else {
      text += it
    }
  }
  return text
}

fun String.toSnakeUpperCase(): String {
  return toSnakeCase().toUpperCase()
}