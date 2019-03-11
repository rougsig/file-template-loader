package com.github.rougsig.filetemplateloader.ui

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_ROOT_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.generatePackageNameByDirectory
import com.github.rougsig.filetemplateloader.extension.generateRootPackageName
import com.github.rougsig.filetemplateloader.extension.getDirectory
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.generator.filterProps
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.generator.setProperty
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileTypes.StdFileTypes
import com.intellij.openapi.ui.Messages

class CreateFileTemplateAnAction(
  private val templateName: String,
  label: String
) : AnAction(
  label,
  null,
  StdFileTypes.UNKNOWN.icon
) {
  override fun actionPerformed(event: AnActionEvent) {
    val project = event.project ?: return
    val dir = event.getDirectory() ?: return

    val template = project.readFileTemplate(templateName)

    val config = project.readConfig()
    config.setProperty(PROP_ROOT_PACKAGE_NAME, generateRootPackageName(config, dir))
    config.setProperty(PROP_PACKAGE_NAME, generatePackageNameByDirectory(config, dir))
    val requiredProps = template.requiredProps.filterProps(config)
    CrateTemplateGroupDialog(
      config,
      requiredProps,
      { props ->
        try {
          project.writeAction("Create ${template.name}") {
            val generatedProps = template.generateProps(props)
            template.create(dir, generatedProps)
          }
        } catch (e: Exception) {
          val stackTrace = e.stackTrace.take(8).joinToString(separator = "\n") { "$it" }
          Messages.showErrorDialog("Creation Failed \n $e \n\n $stackTrace", "File Templates")
        }
      },
      project
    ).show()
  }
}

