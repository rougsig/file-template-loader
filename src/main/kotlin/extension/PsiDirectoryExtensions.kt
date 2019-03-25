package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_ROOT_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.requireProperty
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.command.undo.DocumentReferenceManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parents
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.util.projectStructure.module
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.util.*

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

  val file = targetDir.createFile(fileName)
  val doc = DocumentReferenceManager.getInstance().create(file.virtualFile).document
    ?: throw IllegalStateException("file creation failed: $fileName")
  doc.setText(content)
  PsiDocumentManager.getInstance(project).commitDocument(doc)

  return PsiDocumentManager.getInstance(project).getPsiFile(doc)!!
}

fun generateRootPackageName(props: Props, dir: PsiDirectory): String {
  val defaultProperties = Properties()
  FileTemplateUtil.fillDefaultProperties(defaultProperties, dir)

  val dirStr = dir.virtualFile.url
  val sourceSetRoot = dir.module?.sourceRoots?.find { dirStr.startsWith(it.url) } ?: return ""
  var rootPackage = sourceSetRoot
  while (rootPackage.isDirectory && rootPackage.children.size == 1) {
    rootPackage = rootPackage.children.first()
  }

  return rootPackage.url
    .replace(sourceSetRoot.url, "")
    .replace("/", ".")
    .removePrefix(".")
    .takeIf { it.isNotBlank() }
    ?: defaultProperties.getProperty(PROP_PACKAGE_NAME, "")
}

fun generatePackageNameByDirectory(props: Props, dir: PsiDirectory): String {
  val packageName = StringBuilder()

  val rootPackageName = props.requireProperty(PROP_ROOT_PACKAGE_NAME)
  packageName.append(rootPackageName)

  val subPackage = dir.getPackage()
    ?.qualifiedName
    ?.replace(rootPackageName, "")
    ?.removePrefix(".")

  if (!subPackage.isNullOrBlank()) {
    packageName.append(".")
    packageName.append(subPackage)
  }

  return packageName.toString()
}
