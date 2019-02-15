package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDirectory
import java.util.*

fun String.mergeTemplate(props: Properties): String {
  val merged = FileTemplateUtil.mergeTemplate(props, this, true)
  return StringUtil.convertLineSeparators(merged)
}

fun FileTemplate.mergeTemplate(props: Properties): String {
  return text.mergeTemplate(props)
}

fun FileTemplate.mergeFileName(props: Properties): String {
  return fileName.mergeTemplate(props)
}

fun FileTemplate.createSubDirs(initialDir: PsiDirectory): PsiDirectory {
  val subDirs = directory?.split("/") ?: return initialDir
  return subDirs.fold(initialDir) { acc, name ->
    acc.findSubdirectory(name) ?: acc.createSubdirectory(name)
  }
}

fun FileTemplate.getPackageNameWithSubDirs(initialPackageName: String): String {
  val subDirs = directory ?: return initialPackageName
  return initialPackageName + "." + subDirs.toPackageCase()
}
