package com.github.rougsig.filetemplateloader.selector

sealed class ElementSelector {
  object FirstChild : ElementSelector()
  object LastChild : ElementSelector()
  data class Index(val index: Int) : ElementSelector()
}
