package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase

abstract class NamedFileTemplate : FileTemplate() {
  abstract val fullName: String

  val name by lazy(LazyThreadSafetyMode.NONE) {
    fullName
      .split(".")
      .let { splitted ->
        if (splitted.last() == splitted.find { it.isNotBlank() }) {
          splitted
        } else {
          splitted.take(splitted.lastIndex)
        }
      }
      .joinToString(".") { it }
  }

  val extension by lazy(LazyThreadSafetyMode.NONE) {
    fullName
      .split(".")
      .filter(String::isNotBlank)
      .let { if (it.size > 1) it.last() else "null" }
  }

  val simpleName by lazy(LazyThreadSafetyMode.NONE) {
    name
      .split(".")
      .filter(String::isNotBlank)
      .joinToString("_") { it }
      .toUpperSnakeCase()
  }

  override fun equals(other: Any?): Boolean {
    return other == fullName
  }

  override fun hashCode(): Int {
    return fullName.hashCode()
  }
}
