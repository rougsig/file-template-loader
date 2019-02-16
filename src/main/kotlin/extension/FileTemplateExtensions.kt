package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.projectStructure.module

// FIXME Move "./" logic to separate function
fun PsiDirectory.createSubDirs(directory: String): PsiDirectory {
  val startDirectory = if (directory.startsWith("./")) {
    val projectDir = project.guessProjectDir()!!
    val moduleDir = projectDir.findChild(module!!.name)
    moduleDir
      ?.let { manager.findDirectory(it)!! }
      ?: manager.findDirectory(projectDir)!!
  } else {
    this
  }
  val subDirs = directory.replace("./", "").split("/")
  return subDirs.fold(startDirectory) { acc, dirName ->
    acc.findSubdirectory(dirName) ?: acc.createSubdirectory(dirName)
  }
}

fun FileTemplateSingle.createSubDirs(initialDir: PsiDirectory): PsiDirectory {
  return directory?.let { initialDir.createSubDirs(it) } ?: initialDir
}

fun FileTemplateSingle.getPackageNameWithSubDirs(initialPackageName: String): String {
  val subDirs = directory ?: return initialPackageName
  return initialPackageName + "." + subDirs.toPackageCase()
}
