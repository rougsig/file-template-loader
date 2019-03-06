package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.setProperty
import com.github.rougsig.filetemplateloader.reader.gson
import com.google.gson.Gson
import com.intellij.openapi.util.io.FileUtil
import java.io.File

val DEFAULT_PROPS: Map<String, String>
  get() = Props().apply {
    setProperty("PACKAGE_NAME", "com.github.rougsig.filetemplateloader")
    setProperty("MODEL_NAME", "Epic")
    setProperty("SCREEN_NAME", "Epic")
    setProperty("FLOW_NAME", "Epic")
    setProperty("REPOSITORY_NAME", "Epic")
    setProperty("ROUTE_PATH", "/epic")
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
