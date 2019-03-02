package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.reader.gson
import com.google.gson.Gson
import com.intellij.openapi.util.io.FileUtil
import java.io.File

val DEFAULT_PROPS: Props
  get() = Props().apply {
    setProperty("ONE_PROP", "OneProp")
    setProperty("TWO_PROP", "TwoProp")
    setProperty("THREE_PROP", "ThreeProp")
    setProperty("DUCK_VOICE", "Quack Quack")
    setProperty("CAT_VOICE", "Meow Meow")
    setProperty("FLOW_NAME", "Epic")
    setProperty("SCREEN_NAME", "Duck")
    setProperty("PACKAGE_NAME", "com.github.rougsig.filetemplateloader")
    setProperty("PACKAGE_BASE", "com.github.rougsig")
  }

fun calculateTestDataPath(): String {
  val userDir = System.getProperty("user.dir")
  val dir = File(userDir ?: ".")
  return FileUtil.toCanonicalPath(dir.absolutePath) + "/testData"
}

fun createUnitTestGson(): Gson {
  return gson
    .newBuilder()
    .serializeNulls()
    .setPrettyPrinting()
    .create()
}
