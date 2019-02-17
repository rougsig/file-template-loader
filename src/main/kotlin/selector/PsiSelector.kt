package com.github.rougsig.filetemplateloader.selector

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.util.PsiTreeUtil

fun PsiElement.select(selector: String): List<PsiElement> {
  val selectors = selector
    .toLowerCase()
    .split(" ")
    .map {
      if (it.contains("+")) it.split("+")
      else listOf(it)
    }

  return selectors
    .fold<List<String>, List<PsiElement>>(listOf(this)) { acc, query ->
      acc.flatMap { parent ->
        PsiTreeUtil.collectElements(parent) {
          val str = when (it) {
            is PsiNamedElement -> "$it[name=${it.name}]"
            else -> "$it"
          }
          query.contains("*") || query.all { q -> str.contains(q, true) }
        }.toList()
      }.toList()
    }
}

fun PsiElement.select(selector: String, elementSelector: ElementSelector): PsiElement? {
  val selected = select(selector)

  if (selected.isEmpty()) return null
  return when (elementSelector) {
    is ElementSelector.FirstChild -> selected.first()
    is ElementSelector.LastChild -> selected.last()
    is ElementSelector.Index -> selected[elementSelector.index]
  }
}
