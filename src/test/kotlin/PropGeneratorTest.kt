package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.PropGenerator
import junit.framework.TestCase

class PropGeneratorTest : TestCase() {
  fun testGeneratorTransformation() {
    val generator = PropGenerator(
      "LOWER_CASE",
      String::toLowerCase
    )

    assertEquals(
      "hello_file_template",
      generator.generate("HELLO_FILE_TEMPLATE")
    )
  }
}
