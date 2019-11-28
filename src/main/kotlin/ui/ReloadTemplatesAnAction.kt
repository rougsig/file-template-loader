package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.ui.FileTemplateLoaderProjectComponent.Companion.reloadTemplates
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class ReloadTemplatesAnAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    try {
      event.project?.let(::reloadTemplates)
      Messages.showInfoMessage("Reloaded Successfully", "File Templates")
    } catch (e: Exception) {
      Messages.showErrorDialog("Load Failed \n${e.message}", "File Templates")
    }
  }
}
