package com.github.rougsig.filetemplateloader.ui

import com.intellij.ide.fileTemplates.ui.CreateFromTemplatePanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel

class CrateTemplateGroupDialog(
  private val defaultProperties: Properties,
  private val unsetProperties: Set<String>,
  private val doOkAction: (Properties) -> Unit,
  project: Project
) : DialogWrapper(project, true) {
  private val attrPanel: CreateFromTemplatePanel = CreateFromTemplatePanel(
    unsetProperties.toTypedArray(),
    false,
    null
  )

  init {
    init()
  }

  override fun createCenterPanel(): JComponent {
    attrPanel.ensureFitToScreen(200, 200)
    val centerPanel = JPanel(GridBagLayout())
    centerPanel.add(
      attrPanel.component,
      GridBagConstraints(
        0, 0, 1, 1, 1.0, 1.0,
        GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,
        Insets(0, 0, 0, 0),
        0, 0
      )
    )
    return centerPanel
  }

  override fun doOKAction() {
    val properties = attrPanel.getProperties(defaultProperties)
    val unsetProperties = unsetProperties.filter { properties.getProperty(it).isNullOrBlank() }
    if (unsetProperties.isEmpty()) {
      doOkAction(properties)
      super.doOKAction()
    } else {
      Messages.showMessageDialog(
        "Fill fields: ${unsetProperties.joinToString { it }}",
        "Validation Error",
        Messages.getErrorIcon()
      )
    }
  }
}
