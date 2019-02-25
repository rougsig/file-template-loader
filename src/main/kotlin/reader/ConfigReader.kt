package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun Project.readConfig(): Props {
  return readConfig(guessProjectDir()!!)
}

fun readConfig(dir: VirtualFile): Props {
  println("Read Config from: $dir")
  val props = Props()

  dir.findChild(FILE_TEMPLATE_FOLDER_NAME)!!
    .findChild(CONFIG_FILE_NAME)!!
    .let { props.load(it.inputStream) }

  return props
}

private const val CONFIG_FILE_NAME = "config.properties"
