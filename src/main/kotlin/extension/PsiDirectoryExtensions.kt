package com.github.rougsig.filetemplateloader.extension

import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
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

fun PsiDirectory.createFileToDirectory(directory: String, fileName: String, content: String): PsiFile {
  val targetDir = createSubDirectoriesByRelativePath(directory)

  val doc = PsiDocumentManager.getInstance(project).getDocument(targetDir.createFile(fileName))!!
  doc.setText(content)
  PsiDocumentManager.getInstance(project).commitDocument(doc)

  return PsiDocumentManager.getInstance(project).getPsiFile(doc)!!
}
