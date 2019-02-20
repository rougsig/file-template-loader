package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.filterNotGenerated
import com.github.rougsig.filetemplateloader.extension.generateModuleSimpleName
import com.github.rougsig.filetemplateloader.extension.generatePackageName
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

    val config = project.readConfig()
    dir.generateModuleSimpleName(config)
    config.generatePackageName()
    val requiredProps = template.getRequiredProps(config).filterNotGenerated()

    CrateTemplateGroupDialog(
      config,
      requiredProps,
      { props ->
        project.writeAction("Create ${template.name}") {
          template.generateProps(props)
          template.create(dir, props)
        }
      },
      project
    ).show()
  }
}

