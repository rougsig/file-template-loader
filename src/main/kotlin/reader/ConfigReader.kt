package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.Props
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun Project.readConfig(): Props {
  return readConfig(guessProjectDir()!!)
}

fun readConfig(dir: VirtualFile): Props {
  println("Read Config from: $dir")
  val config = Props()
  val fileTemplates = dir.findChild(FILE_TEMPLATE_FOLDER_NAME) ?: return config
  fileTemplates.findChild(CONFIG_FILE_NAME)?.let { config.load(it.inputStream) }
  return config
}

private const val CONFIG_FILE_NAME = "config.properties"
