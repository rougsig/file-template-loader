package com.github.rougsig.filetemplateloader.reader

import com.intellij.openapi.vfs.VirtualFile
import java.util.*

fun readConfig(dir: VirtualFile): Properties {
  val config = Properties()
  dir.findChild(CONFIG_FILE_NAME)?.let { config.load(it.inputStream) }
  return config
}

private const val CONFIG_FILE_NAME = "config.properties"
