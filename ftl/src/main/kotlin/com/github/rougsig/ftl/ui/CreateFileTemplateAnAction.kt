package com.github.rougsig.ftl.ui

import com.github.rougsig.ftl.extenstion.getDirectory
import com.github.rougsig.ftl.extenstion.writeAction
import com.github.rougsig.ftl.io.DirectoryImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import kotlin.reflect.KFunction

internal class CreateFileTemplateAnAction(
  label: String,
  private val template: KFunction<Unit>
) : AnAction(
  label,
  null,
  null
) {
  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project
    if (project == null) {
      Messages.showErrorDialog("Creation Failed \n project is null", "File Templates")
      return
    }

    val dir = event.getDirectory()
    if (dir == null) {
      Messages.showErrorDialog("Creation Failed \n dir is null", "File Templates")
      return
    }

    val params = template.parameters.drop(1).mapNotNull { it.name }
    CreateTemplateGroupDialog(
      params.toSet(),
      { props ->
        try {
          project.writeAction {
            template.call(DirectoryImpl.find(project, dir), *params.map { props[it] }.toTypedArray())
          }
        } catch (e: Exception) {
          val stackTrace = e.stackTrace.joinToString(separator = "\n") { "$it" }
          Messages.showErrorDialog("Creation Failed \n $e \n\n $stackTrace", "File Templates")
        }
      },
      project
    ).show()
  }
}
