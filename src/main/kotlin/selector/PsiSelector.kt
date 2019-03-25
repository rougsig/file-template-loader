package com.github.rougsig.filetemplateloader.selector

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.impl.source.tree.LeafElement
import com.intellij.psi.util.PsiTreeUtil

fun PsiElement.select(selector: String): PsiElement? {
  val selectors = selector
    .toLowerCase()
    .split(" ")
    .map {
      if (it.contains("+")) it.split("+")
      else listOf(it)
    }
  return selectors
    .fold(listOf(this.navigationElement)) { acc, query ->
      acc.flatMap { parent ->
        PsiTreeUtil.collectElements(parent) {
          val builder = StringBuilder()
          builder.append("$it[")
          when (it) {
            is PsiNamedElement -> builder.append(" name=${it.name} ")
            is LeafElement -> builder.append(" text=${it.chars} ")
          }
          builder.append("]")
          val str = builder.toString()
          query.contains("*") || query.all { q -> str.contains(q, true) }
        }.toList()
      }.toList()
    }.lastOrNull()
}
