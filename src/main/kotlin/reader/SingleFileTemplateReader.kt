package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.intellij.openapi.vfs.VirtualFile

fun readSingleFileTemplate(file: VirtualFile): FileTemplateSingle {
  return FileTemplateSingle(
    name = file.nameWithoutExtension,
    text = String(file.inputStream.readBytes())
  )
}
