package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.toUpperSnakeCase
import com.github.rougsig.filetemplateloader.reader.FILE_NAME_DELIMITER

abstract class NamedFileTemplate : FileTemplate() {
  abstract val name: String

  val simpleName by lazy(LazyThreadSafetyMode.NONE) {
    (name
      .split(FILE_NAME_DELIMITER)
      .find { it.isNotBlank() && it != FILE_NAME_DELIMITER } ?: name)
      .toUpperSnakeCase()
  }

  override fun equals(other: Any?): Boolean {
    return other == name
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
