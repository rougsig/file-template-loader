package com.github.rougsig.filetemplateloader.entity

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateEntry(
  val text: String,
  val selector: String,
  val className: String? = null,
  val pathName: String? = null,
  override val name: String = ""
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateEntry: \n text: $text \n dir: $dir \n props: $props \n")

    return emptyList()
  }

  override fun getAllProps(): Set<String> {
    return getTemplateProps(text)
  }

  override fun generateProps(props: Properties) {
    generateProps(getRequiredProps(props), props)
  }

  override val isSourceCode: Boolean = false
}
