package com.github.rougsig.filetemplateloader

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.util.io.FileUtil
import java.io.File

fun calculateTestDataPath(): String {
  val userDir = System.getProperty("user.dir")
  val dir = File(userDir ?: ".")
  return FileUtil.toCanonicalPath(dir.absolutePath) + "/testData"
}

fun createGson(): Gson {
  return GsonBuilder()
    .serializeNulls()
    .setPrettyPrinting()
    .setExclusionStrategies(object : ExclusionStrategy {
      override fun shouldSkipField(p0: FieldAttributes): Boolean {
        return p0.name == "simpleName\$delegate" || p0.name == "requiredProps"
      }

      override fun shouldSkipClass(p0: Class<*>?): Boolean {
        return false
      }
    })
    .create()
}
