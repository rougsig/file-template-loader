package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_EXTENSION
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplateModules
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
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

        projects.forEach { project ->
          val templates = project.readFileTemplates()
          val templateGroups = project.readFileTemplateGroups(templates, gson)
          val templateModules = project.readFileTemplateModules(templates, gson)

          val projectGroup = DefaultActionGroup(project.name, true)
          val templateGroup = DefaultActionGroup("template", true)
          val moduleGroup = DefaultActionGroup("module", true)
          val groupGroup = DefaultActionGroup("group", true)

          projectGroup.addAction(groupGroup)
          projectGroup.addAction(moduleGroup)
          projectGroup.addAction(templateGroup)

          templates
            .forEach { template ->
              templateGroup.add(CreateFileTemplateAnAction(template))
            }

          templateGroups
            .forEach { template ->
              groupGroup.add(CreateFileTemplateAnAction(template))
            }

          templateModules
            .forEach { template ->
              moduleGroup.add(CreateFileTemplateAnAction(template))
            }

          templatesGroup.add(projectGroup)
        }
    }
  }

  init {
    println("Init: FileTemplateLoaderProjectComponent")
    projects.forEach {
      it.writeAction {
        FileTypeManager.getInstance().associatePattern(PlainTextFileType.INSTANCE, "*.$FILE_TEMPLATE_EXTENSION")
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
