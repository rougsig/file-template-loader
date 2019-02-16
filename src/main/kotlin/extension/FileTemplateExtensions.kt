package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.intellij.psi.PsiDirectory
import com.intellij.psi.util.parents

// FIXME Move "./" logic to separate function
fun PsiDirectory.createSubDirs(directory: String): PsiDirectory {
  val startDirectory = if (directory.startsWith("./")) {
    parents().last() as PsiDirectory
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
