package com.github.rougsig.filetemplateloader.entity

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateFolder(
  val pathName: String,
  val templates: List<FileTemplateSingle>,
  override val name: String = ""
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