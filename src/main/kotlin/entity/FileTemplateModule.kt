package com.github.rougsig.filetemplateloader.entity

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateModule(
  val moduleName: String,
  override val name: String,
  val group: FileTemplateGroup
) : FileTemplate by group {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}