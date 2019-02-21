package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.constant.REQUIRED_CONFIG_PROPS
import com.github.rougsig.filetemplateloader.entity.Props
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun Project.readConfig(): Props {
  return readConfig(guessProjectDir()!!)
}

fun readConfig(dir: VirtualFile): Props {
  println("Read Config from: $dir")
  val props = Props()

  dir.findChild(FILE_TEMPLATE_FOLDER_NAME)?.let {
    parseConfigTo(props, it)
    validateConfig(props)
  }

  return props
}

private fun validateConfig(props: Props) {
  val values = props.keys as Set<String>
  val requiredProps = REQUIRED_CONFIG_PROPS.filterNot { values.contains(it) }

  if (requiredProps.isNotEmpty()) {
    throw IllegalStateException("properties not found in $FILE_TEMPLATE_FOLDER_NAME/$CONFIG_FILE_NAME: ${requiredProps.joinToString { it }}")
  }
}

private fun parseConfigTo(props: Props, dir: VirtualFile?) {
  dir?.findChild(CONFIG_FILE_NAME)?.let { props.load(it.inputStream) }
}

private const val CONFIG_FILE_NAME = "config.properties"
