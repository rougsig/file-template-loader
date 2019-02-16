package com.github.rougsig.filetemplateloader.extension

import com.intellij.ide.util.DirectoryChooserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.psi.PsiDirectory

fun AnActionEvent.getDirectory(): PsiDirectory? {
  val view = LangDataKeys.IDE_VIEW.getData(this.dataContext) ?: return null
  return DirectoryChooserUtil.getOrChooseDirectory(view) ?: return null
}
