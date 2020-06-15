package com.github.rougsig.ftl

import com.github.rougsig.ftl.extenstion.ftlTemplateDir
import com.github.rougsig.ftl.kts.KtsRunner
import com.github.rougsig.ftl.kts.compile
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class ReloadTemplatesAnAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    try {
      val project = event.project ?: error("Project is null")
      val templates = project.ftlTemplateDir
      val runner = KtsRunner()
      runner.compile(templates)
      val templatesFuns = runner.invokeFunction<List<Template>>("templates")
      templatesFuns.forEach(::println)
      Messages.showInfoMessage("Reloaded Successfully", "File Templates")
    } catch (e: Exception) {
      Messages.showErrorDialog(
        "Load Failed\n${e.message}\n${e.stackTrace.toList().joinToString("\n")}",
        "File Templates"
      )
    }
  }
}
