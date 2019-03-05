package com.github.rougsig.filetemplateloader.extension

import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.projectStructure.module

fun PsiDirectory.createSubDirectoriesByRelativePath(path: String): PsiDirectory {
  if (path.isBlank() || path == "/") return this
  val startDirectory = if (path.startsWith("./")) {
    val projectDir = project.guessProjectDir()!!
    val moduleDir = projectDir.findChild(module!!.name)
    moduleDir
      ?.let { manager.findDirectory(it)!! }
      ?: manager.findDirectory(projectDir)!!
  } else {
    this
  }
  val subDirs = path.replace("./", "").split("/").filter(String::isNotBlank)
  return subDirs.fold(startDirectory) { acc, dirName ->
    acc.findSubdirectory(dirName) ?: acc.createSubdirectory(dirName)
  }
}
