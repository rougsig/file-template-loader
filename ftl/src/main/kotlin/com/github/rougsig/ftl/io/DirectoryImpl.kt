package com.github.rougsig.ftl.io

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile


internal class DirectoryImpl private constructor(private val virtualFile: VirtualFile) :
  Directory {
  companion object {
    fun find(path: String): Directory {
      val dir = LocalFileSystem.getInstance().findFileByPath(path) ?: error("directory not found by path $path")
      return DirectoryImpl(dir)
    }

    fun find(virtualFile: VirtualFile): Directory {
      return DirectoryImpl(virtualFile)
    }
  }

  override val path: String
    get() = virtualFile.path

  override fun createDirectory(name: String): Directory {
    val dir = virtualFile.findChild(name)
      ?: virtualFile.createChildDirectory(null, name)
    return DirectoryImpl(dir)
  }

  override fun createFile(nameWithExtension: String): File {
    val file = virtualFile.findChild(nameWithExtension)
      ?: virtualFile.createChildData(null, nameWithExtension)
    return FileImpl.find(file)
  }
}

internal fun Directory.toVirtualFile(): VirtualFile {
  return LocalFileSystem.getInstance().findFileByPath(path)!!
}
