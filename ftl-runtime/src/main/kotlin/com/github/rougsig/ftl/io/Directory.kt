package com.github.rougsig.ftl.io

interface Directory {
  companion object

  val path: String
  fun createDirectory(name: String): Directory
  fun createFile(nameWithExtension: String): File

  val projectRoot: Directory
  val moduleRoot: Directory?
  val sourceRoot: Directory?
}
