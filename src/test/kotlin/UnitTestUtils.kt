package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.reader.FileTemplateSingleAdapter
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
    .registerTypeAdapter(FileTemplateSingle::class.java, FileTemplateSingleAdapter())
    .create()
}
