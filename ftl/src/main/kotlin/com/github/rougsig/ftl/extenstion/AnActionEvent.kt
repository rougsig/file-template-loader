package com.github.rougsig.ftl.extenstion

import com.intellij.ide.util.DirectoryChooserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.vfs.VirtualFile

internal fun AnActionEvent.getDirectory(): VirtualFile? {
  val view = LangDataKeys.IDE_VIEW.getData(this.dataContext) ?: return null
  return DirectoryChooserUtil.getOrChooseDirectory(view)?.virtualFile ?: return null
}
