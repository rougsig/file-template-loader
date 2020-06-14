package com.github.rougsig.ftl

import com.github.rougsig.ftl.io.DirectoryImpl
import com.github.rougsig.ftl.io.toVirtualFile
import com.github.rougsig.ftl.kts.KtsRunner
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

class ReloadTemplatesAnAction : AnAction() {
  override fun actionPerformed(event: AnActionEvent) {
    try {
      val project = event.project ?: error("Project is null")
      val basePath = project.basePath ?: error("project baseDir is null")
      val moduleDir = DirectoryImpl.find(basePath)
        .createDirectory(MODULE_DIR_NAME)
      val templates = moduleDir
        .createDirectory(TEMPLATE_DIR_NAME)
        .toVirtualFile()
      val runner = KtsRunner(moduleDir.createDirectory("lib").toVirtualFile().children.map { File(it.path) })
      runner.loadVirtualFile(templates)
      runner.invokeFunction("templates")
      Messages.showInfoMessage("Reloaded Successfully", "File Templates")
    } catch (e: Exception) {
      Messages.showErrorDialog(
        "Load Failed\n${e.message}\n${e.stackTrace.toList().joinToString("\n")}",
        "File Templates"
      )
    }
  }

  private fun KtsRunner.loadVirtualFile(file: VirtualFile) {
    if (file.isDirectory) file.children.forEach { loadVirtualFile(it) }
    else FileDocumentManager.getInstance().getDocument(file)?.text?.let { load(it) }
  }
}
