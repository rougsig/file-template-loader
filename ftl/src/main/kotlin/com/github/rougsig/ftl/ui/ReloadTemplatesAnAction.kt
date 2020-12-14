package com.github.rougsig.ftl.ui

import com.github.rougsig.ftl.FtlProjectModule
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ReloadTemplatesAnAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: error("Project is null")
    FtlProjectModule.getInstance(project).reloadTemplates(silent = false)
  }
}