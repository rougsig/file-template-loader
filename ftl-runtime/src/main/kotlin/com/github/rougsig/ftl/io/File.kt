package com.github.rougsig.ftl.io

interface File {
  companion object

  val nameWithExtension: String
  val name: String
  val path: String
  val pathName: String
  fun write(text: String)
}
