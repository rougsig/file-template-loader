package com.github.rougsig.ftl.io

import com.github.rougsig.ftl.extenstion.findModule
import com.github.rougsig.ftl.extenstion.findModuleRoot
import com.github.rougsig.ftl.extenstion.findSourceRoot
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

internal class DirectoryImpl private constructor(
  private val project: Project,
  private val virtualFile: VirtualFile
) : Directory {
  companion object {
    fun find(project: Project, path: String): Directory {
      val dir = LocalFileSystem.getInstance().findFileByPath(path) ?: error("directory not found by path $path")
      return DirectoryImpl(project, dir)
    }

    fun find(project: Project, virtualFile: VirtualFile): Directory {
      return DirectoryImpl(project, virtualFile)
    }
  }

  override val path: String
    get() = virtualFile.path
  override val moduleRoot: Directory?
    get() = virtualFile.findModule(project)?.findModuleRoot()
  override val projectRoot: Directory
    get() = find(project, project.basePath!!)
  override val sourceRoot: Directory?
    get() = virtualFile.findModule(project)?.findSourceRoot()

  override fun createDirectory(name: String): Directory {
    val dir = virtualFile.findChild(name)
      ?: virtualFile.createChildDirectory(null, name)
    return DirectoryImpl(project, dir)
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
