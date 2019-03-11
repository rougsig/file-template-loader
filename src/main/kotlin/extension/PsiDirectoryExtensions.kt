package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_BASE
import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parents
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.util.projectStructure.module

fun PsiDirectory.createSubDirectoriesByRelativePath(path: String): PsiDirectory {
  if (path.isBlank()) return this
  val projectDir = project.guessProjectDir()!!
  val projectDirParents = manager.findDirectory(projectDir)!!.parents().toList()
  val currentDirParent = parents().toList()
  val parentsDiff = currentDirParent.minus(projectDirParents).reversed()
  val startDirectory = when {
    path.startsWith("./") -> {
      // Find module root
      val moduleDir = projectDir.findChild(module!!.name)
      moduleDir
        ?.let { manager.findDirectory(it)!! }
        ?: parentsDiff.firstOrNull() as? PsiDirectory
        ?: manager.findDirectory(projectDir)!!
    }
    path.startsWith("/") -> {
      // Find project root
      manager.findDirectory(projectDir)!!
    }
    else -> this
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

fun generatePackageName(props: Props, dir: PsiDirectory): String {
  val packageName = StringBuilder()

  val basePackageName = props.getOrDefault(PROP_PACKAGE_BASE, "")
  if (!basePackageName.isBlank()) {
    packageName.append(basePackageName)
  }

  val moduleName = dir.module
    ?.let { ModuleUtil.getModuleDirPath(it) }
    ?.replace("\\", "/")
    ?.split("/")
    ?.last()
    ?.toDotCase()
  if (!moduleName.isNullOrBlank()) {
    packageName.append(".")
    packageName.append(moduleName)
  }

  val subPackage = dir.getPackage()?.qualifiedName
  if (!subPackage.isNullOrBlank()) {
    packageName.append(".")
    packageName.append(subPackage)
  }

  return packageName.toString()
}
