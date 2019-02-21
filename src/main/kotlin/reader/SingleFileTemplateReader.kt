package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.SingleFileTemplate
import com.intellij.openapi.vfs.VirtualFile

fun readSingleFileTemplate(file: VirtualFile): SingleFileTemplate {
  val slittedName = file.nameWithoutExtension.split(FILE_NAME_DELIMITER)
  return SingleFileTemplate(
    name = slittedName.first(),
    extension = slittedName.last(),
    text = String(file.inputStream.readBytes())
  )
}
