package com.github.rougsig.filetemplateloader.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class ReloadTemplatesAnAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    FileTemplateLoaderProjectComponent.reloadTemplates()
    Messages.showInfoMessage("Reloaded Successfully", "File Templates")
  }
}
