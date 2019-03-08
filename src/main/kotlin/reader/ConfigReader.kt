package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile
import java.util.*

fun Project.readConfig(): Props {
  return readConfig(guessProjectDir()!!)
}

fun readConfig(dir: VirtualFile): Props {
  println("\nRead Config FROM: `$dir`\n")
  val properties = Properties()

  dir
    .findChild(FILE_TEMPLATE_FOLDER_NAME)!!
    .findChild(CONFIG_FILE_NAME)!!
    .let { properties.load(it.inputStream) }

  return Props().apply { putAll(properties as Map<String, String>) }
}
