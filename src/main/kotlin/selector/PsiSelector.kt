package com.github.rougsig.filetemplateloader.selector

import com.intellij.psi.PsiElement

fun PsiElement.select(selector: String): List<PsiElement> {
  return selector.split(" ")
    .map { it.toLowerCase() }
    .fold(listOf(this)) { acc, query ->
      acc.flatMap { child ->
        child.children.filter {
          it.toString().toLowerCase().contains(query)
        }
      }
    }
}

fun PsiElement.select(selector: String, elementSelector: ElementSelector): PsiElement {
  val selected = select(selector)
  return when (elementSelector) {
    is ElementSelector.FirstChild -> selected.first()
    is ElementSelector.LastChild -> selected.last()
    is ElementSelector.Index -> selected[elementSelector.index]
  }
}
