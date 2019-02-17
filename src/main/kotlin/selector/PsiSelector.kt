package com.github.rougsig.filetemplateloader.selector

import com.intellij.psi.PsiElement

fun PsiElement.select(selector: String): List<PsiElement> {
  return selector.split(" ")
    .fold(listOf(this)) { acc, query ->
      acc.flatMap { child -> child.children.filter { it.toString().contains(query) } }
    }
}

sealed class ElementSelector {
  object FirstChild : ElementSelector()
  object LastChild : ElementSelector()
  data class Index(val index: Int) : ElementSelector()
}

fun PsiElement.select(selector: String, elementSelector: ElementSelector): PsiElement {
  val selected = select(selector)
  return when (elementSelector) {
    is ElementSelector.FirstChild -> selected.first()
    is ElementSelector.LastChild -> selected.last()
    is ElementSelector.Index -> selected.get(elementSelector.index)
  }
}
