package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.projectStructure.module

// FIXME Move "./" logic to separate function
fun PsiDirectory.createDirectoriesByRelativePath(path: String): PsiDirectory {
  val dir = path.replace("\\", "/")
  if (dir.isBlank() || dir == "/") return this

  val startDirectory = if (dir.startsWith("./")) {
    val projectDir = project.guessProjectDir()!!
    val moduleDir = projectDir.findChild(module!!.name)
    moduleDir
      ?.let { manager.findDirectory(it)!! }
      ?: manager.findDirectory(projectDir)!!
  } else {
    this
  }
  val subDirs = dir.replace("./", "").split("/")
  return subDirs.fold(startDirectory) { acc, dirName ->
    acc.findSubdirectory(dirName) ?: acc.createSubdirectory(dirName)
  }
}

fun FileTemplateSingle.getPackageNameWithSubDirs(initialPackageName: String): String {
  val subDirs = directory ?: return initialPackageName
  return initialPackageName + "." + subDirs.toPackageCase()
}
