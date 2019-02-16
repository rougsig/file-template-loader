package com.github.rougsig.filetemplateloader.entity

data class PropGenerator(
  val name: String,
  private val generator: (String) -> String
) {
  fun generate(str: String) = generator(str)
}
