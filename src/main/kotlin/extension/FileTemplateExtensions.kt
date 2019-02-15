package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.idea.util.projectStructure.getModuleDir
import java.util.*

fun String.mergeTemplate(props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, this, true)
  return StringUtil.convertLineSeparators(merged)
}

// FIXME Move "./" logic to separate function
fun PsiDirectory.createSubDirs(directory: String): PsiDirectory {
  val startDirectory = if (directory.startsWith("./")) {
    val moduleRoot = LocalFileSystem.getInstance().findFileByPath(module!!.getModuleDir())!!
    manager.findDirectory(moduleRoot)!!
  } else {
    this
  }
  val subDirs = directory.replace("./", "").split("/")
  return subDirs.fold(startDirectory) { acc, dirName ->
    acc.findSubdirectory(dirName) ?: acc.createSubdirectory(dirName)
  }
}

fun FileTemplate.mergeTemplate(props: Properties): String {
  return text.mergeTemplate(props)
}

fun FileTemplate.mergeFileName(props: Properties): String {
  return fileName.mergeTemplate(props)
}

fun FileTemplate.createSubDirs(initialDir: PsiDirectory): PsiDirectory {
  return directory?.let { initialDir.createSubDirs(it) } ?: initialDir
}

fun FileTemplate.getPackageNameWithSubDirs(initialPackageName: String): String {
  val subDirs = directory ?: return initialPackageName
  return initialPackageName + "." + subDirs.toPackageCase()
}
