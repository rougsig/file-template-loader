package com.github.rougsig.ftl.io

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

internal class FileImpl private constructor(private val virtualFile: VirtualFile) :
  File {
  companion object {
    fun find(nameWithExtension: String): File {
      val file = LocalFileSystem.getInstance().findFileByPath(nameWithExtension)
        ?: error("file not found by name $nameWithExtension")
      return FileImpl(file)
    }

    fun find(virtualFile: VirtualFile): File {
      return FileImpl(virtualFile)
    }
  }

  override val nameWithExtension: String
    get() = virtualFile.name
  override val name: String
    get() = virtualFile.nameWithoutExtension
  override val path: String
    get() = virtualFile.path.dropLast("/$nameWithExtension".length)
  override val pathName: String
    get() = virtualFile.path

  override fun writeText(text: String) {
    val document = FileDocumentManager.getInstance().getDocument(virtualFile)
      ?: error("document not found by path ${virtualFile.path}")
    document.setText(text)
  }
}

internal fun File.toVirtualFile(): VirtualFile {
  return LocalFileSystem.getInstance().findFileByPath(path)!!
}
