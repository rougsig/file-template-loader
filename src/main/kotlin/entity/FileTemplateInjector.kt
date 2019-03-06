package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.extension.mergeTemplate
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.selector.select
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.psiUtil.endOffset

data class FileTemplateInjector(
  private val text: String,
  val selector: String,
  val className: String? = null,
  val pathName: String? = null
) : FileTemplate() {
  override val extractedProps: Set<String> =
    extractProps(text)
      .plus(extractProps(selector))
      .plus(extractProps(className ?: ""))
      .plus(extractProps(pathName ?: ""))

  override val requiredProps: Set<String>
    get() = extractedProps

  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    val insertTo = getInsertTo(dir.project, props).containingFile
    val selected = insertTo.select(selector)!!
    val ext = mergeTemplate(text, props)

    val doc = PsiDocumentManager.getInstance(dir.project).getDocument(insertTo)!!
    doc.insertString(selected.endOffset, ext)
    PsiDocumentManager.getInstance(dir.project).commitDocument(doc)

    val resultFile = CodeStyleManager.getInstance(dir.manager)
      .reformat(PsiDocumentManager.getInstance(dir.project).getPsiFile(doc)!!)
      .containingFile

    return listOf(resultFile)
  }

  private fun getInsertTo(project: Project, props: Props): PsiElement {
    if (className != null) {
      return JavaPsiFacade.getInstance(project)
        .findClass(mergeTemplate(className, props), GlobalSearchScope.allScope(project))!!
        .containingFile
    }

    if (pathName != null) {
      val file = project.guessProjectDir()!!.findFileByRelativePath(mergeTemplate(pathName, props))!!
      return PsiManager.getInstance(project).findFile(file)!!
    }

    throw IllegalStateException("can't create entry: className == null && directory == null")
  }
}
