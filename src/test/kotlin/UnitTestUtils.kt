package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.createGson
import com.google.gson.Gson
import com.intellij.openapi.util.io.FileUtil
import java.io.File

const val PROP_PACKAGE_NAME_TEST_VALUE = "com.github.rougsig.filetemplateloader"

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
