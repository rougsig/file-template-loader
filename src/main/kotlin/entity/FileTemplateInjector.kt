package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.selector.select
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import java.util.*

data class FileTemplateInjector(
  val text: String,
  val selector: String,
  val className: String? = null,
  val pathName: String? = null,
  override val name: String = ""
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Props): List<PsiFile> {
    println("Create FileTemplateInjector: \n text: $text \n dir: $dir \n extractedProps: $props \n selector: $selector \n")

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

  override fun getAllProps(): Set<String> {
    return extractProps(text)
  }

  override fun generateProps(props: Properties) {
    generateProps(getRequiredProps(props), props)
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

  override val hasClassName: Boolean = false
}
