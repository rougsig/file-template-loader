package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.entity.ScopedFileTemplate
import com.github.rougsig.filetemplateloader.extension.getDirectory
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.generator.filterProps
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileTypes.StdFileTypes

class CreateFileTemplateAnAction(
  private val template: ScopedFileTemplate
) : AnAction(
  template.name,
  null,
  StdFileTypes.UNKNOWN.icon
) {
  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    val dir = event.getDirectory() ?: return

    val config = project.readConfig()
    val requiredProps = template.requiredProps.filterProps(config)
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

