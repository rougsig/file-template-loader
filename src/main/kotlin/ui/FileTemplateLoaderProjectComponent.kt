package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_FT_EXTENSION
import com.google.gson.Gson
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager

class FileTemplateLoaderProjectComponent : ProjectComponent {

  companion object {
    private val gson = Gson()
    private val projects: List<Project>
      get() = ProjectManager.getInstance().openProjects.toList()

    fun reloadTemplates() {
      println("Init: ReloadTemplates")

      val templatesGroup =
        ActionManager.getInstance().getAction(CreateFromProjectTemplateAnAction.ID) as DefaultActionGroup
      templatesGroup.removeAll()
    }
  }

  init {
    println("Init: FileTemplateLoaderProjectComponent")
    projects.forEach {
      it.writeAction {
        FileTypeManager.getInstance().associatePattern(PlainTextFileType.INSTANCE, "*.$FILE_TEMPLATE_FT_EXTENSION")
      }
    }
  }

  override fun projectOpened() {
    reloadTemplates()
  }

  override fun projectClosed() {
    reloadTemplates()
  }
}
