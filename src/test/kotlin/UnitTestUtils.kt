package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.createGson
import com.google.gson.Gson
import com.intellij.openapi.util.io.FileUtil
import java.io.File

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
