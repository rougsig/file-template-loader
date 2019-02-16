package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.extension.getDirectory
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileTypes.StdFileTypes

class CreateFileTemplateAnAction(
  private val template: FileTemplate
) : AnAction(
  template.name,
  null,
  StdFileTypes.UNKNOWN.icon
) {
  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    val dir = event.getDirectory() ?: return

//    val defaultProperties = getDefaultFileTemplateProperties(project, directory)
//    val unsetProperties = defaultProperties.getUnsetProperties(templates)

    val config = project.readConfig()
    val requiredProps = template.getRequiredProps(config, true)

    CrateTemplateGroupDialog(
      config,
      requiredProps,
      { props ->
        project.writeAction {
          template.generateProps(props)
          template.create(dir, props)
        }
      },
      project
    ).show()
  }
}

