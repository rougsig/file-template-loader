package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.reader.createGson
import com.google.gson.Gson
import com.intellij.openapi.util.io.FileUtil
import java.io.File

const val PROP_PACKAGE_NAME_TEST_VALUE = "com.github.rougsig.filetemplateloader"

val DEFAULT_PROPS: Props
  get() = Props().apply {
    setProperty("ONE_PROP", "OneProp")
    setProperty("TWO_PROP", "TwoProp")
    setProperty("THREE_PROP", "ThreeProp")
    setProperty("DUCK_VOICE", "Quack Quack")
    setProperty("CAT_VOICE", "Meow Meow")
    setProperty(PROP_PACKAGE_NAME, PROP_PACKAGE_NAME_TEST_VALUE)
  }

fun calculateTestDataPath(): String {
  val userDir = System.getProperty("user.dir")
  val dir = File(userDir ?: ".")
  return FileUtil.toCanonicalPath(dir.absolutePath) + "/testData"
}

fun createUnitTestGson(): Gson {
  return createGson()
    .newBuilder()
    .serializeNulls()
    .setPrettyPrinting()
    .create()
}
