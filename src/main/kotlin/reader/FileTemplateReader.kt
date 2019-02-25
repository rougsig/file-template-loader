package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.readFileTemplate(templateName: String): FileTemplate {
  val templateFile = fileRec.find { it.name == templateName }
    ?: throw IllegalStateException("template not found: $templateName")

  return when (templateName) {
    else -> readSingleFileTemplate(templateFile)
  }
}

private fun readSingleFileTemplate(file: VirtualFile): FileTemplateSingle {
  val nameWithoutExtension = FILE_TEMPLATE_EXTENSION_MATCHER.replace(file.name) { "" }
  return FileTemplateSingle(
    name = nameWithoutExtension,
    text = String(file.inputStream.readBytes())
  )
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }
