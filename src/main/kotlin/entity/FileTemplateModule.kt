package com.github.rougsig.filetemplateloader.entity

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateModule(
  override val name: String,
  val moduleName: String,
  val folders: List<FileTemplateFolder>,
  val entries: List<FileTemplateEntry>
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getAllProps(): Set<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun generateProps(props: Properties) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override val hasClassName: Boolean = false
}