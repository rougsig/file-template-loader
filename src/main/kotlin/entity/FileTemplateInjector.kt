package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

data class FileTemplateInjector(
  private val text: String,
  private val selector: String,
  private val className: String? = null,
  private val pathName: String? = null
) : FileTemplate() {
  override val extractedProps: Set<String> =
    extractProps(text)
      .plus(extractProps(selector))
      .plus(extractProps(className ?: ""))
      .plus(extractProps(pathName ?: ""))

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
