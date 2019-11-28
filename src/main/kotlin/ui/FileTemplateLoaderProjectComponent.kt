package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_CONFIG_EXTENSION
import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_FOLDER_NAME
import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_MIXIN_EXTENSION
import com.github.rougsig.filetemplateloader.reader.FT_EXTENSION
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import org.apache.velocity.app.Velocity
import org.apache.velocity.runtime.RuntimeSingleton

class FileTemplateLoaderProjectComponent(project: Project) : ProjectComponent {
  companion object {
    fun reloadTemplates(project: Project) {
      println("Init: ReloadTemplates")
      val templatesGroup = ActionManager.getInstance().getAction("NewGroup") as DefaultActionGroup

      try {
        val fileTemplateDirectory = project
          .guessProjectDir()!!
          .findChild(FILE_TEMPLATE_FOLDER_NAME)

        fun buildGroup(
          file: VirtualFile,
          group: DefaultActionGroup,
          groupName: String? = null,
          constraint: Constraints = Constraints.LAST
        ) {
          if (file.isDirectory) file.children.toList()
            .filterNot {
              it.name.endsWith(FILE_TEMPLATE_MIXIN_EXTENSION)
                || it.name.endsWith(FILE_TEMPLATE_CONFIG_EXTENSION)
            }
            .sortedByDescending { it.isDirectory }
            .let { children ->
              val childGroup = DefaultActionGroup(groupName ?: file.name, true)
              group.add(childGroup, constraint)
              children.forEach { buildGroup(it, childGroup) }
            }
          else group.add(CreateFileTemplateAnAction(file.name, file.nameWithoutExtension), constraint)
        }

        if (fileTemplateDirectory != null) {
          buildGroup(fileTemplateDirectory, templatesGroup, "From Project Template", Constraints.FIRST)
        }
      } catch (e: Exception) {
        val stackTrace = e.stackTrace.take(8).joinToString(separator = "\n") { "$it" }
        Messages.showErrorDialog("Load Failed \n $e \n\n $stackTrace", "File Templates")
      }
    }
  }

  init {
    println("Init: FileTemplateLoaderProjectComponent")
    if (RuntimeSingleton.isInitialized()) Velocity.init()
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(PlainTextFileType.INSTANCE, "*.$FT_EXTENSION")
    }
    reloadTemplates(project)
  }
}
