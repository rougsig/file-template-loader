package com.github.rougsig.ftl

import com.github.rougsig.ftl.dsl.FtlMenuBuilder
import com.github.rougsig.ftl.dsl.MenuItem
import com.github.rougsig.ftl.extenstion.ftlTemplateDir
import com.github.rougsig.ftl.extenstion.writeAction
import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.io.toVirtualFile
import com.github.rougsig.ftl.ktsrunner.KtsRunner
import com.github.rougsig.ftl.ui.CreateFileTemplateAnAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.jetbrains.rd.util.string.printToString
import kotlin.reflect.jvm.javaType

class FtlProjectModule(private val project: Project) : ProjectComponent {

  companion object {
    fun getInstance(project: Project): FtlProjectModule {
      return project.getComponent(FtlProjectModule::class.java)
    }
  }

  private val ftlGroup = DefaultActionGroup("From FTL", true)

  override fun projectOpened() {
    project.writeAction { createFtlModule(project) }
    val createNewGroup = ActionManager.getInstance().getAction("NewGroup") as DefaultActionGroup
    createNewGroup.add(ftlGroup, Constraints.FIRST)
    reloadTemplates()
  }

  override fun projectClosed() {
    val createNewGroup = ActionManager.getInstance().getAction("NewGroup") as DefaultActionGroup
    createNewGroup.remove(ftlGroup)
  }

  fun reloadTemplates(silent: Boolean = true) {
    try {
      val main = project.ftlTemplateDir.createFile("ftl.main.kts")
      val runner = KtsRunner()
      runner.compile(java.io.File(main.pathName).readText())

      val menuBuilder = FtlMenuBuilder()
      runner.invokeFunction<Unit>("buildMenu", menuBuilder)
      val items = menuBuilder.build()
      validateMenuItems(items)

      ftlGroup.removeAll()
      buildMenu(ftlGroup, items)
      if (!silent) Messages.showInfoMessage("Reloaded Successfully", "File Templates")
    } catch (e: Exception) {
      if (!silent) Messages.showErrorDialog(
        "Load Failed\n${e.printToString()}",
        "File Templates"
      )
    }
  }

  private fun validateMenuItems(items: List<MenuItem>) {
    fun flatItems(items: List<MenuItem>, target: MutableList<MenuItem.Item> = mutableListOf()): List<MenuItem.Item> {
      items.forEach { item ->
        when (item) {
          is MenuItem.Group -> flatItems(item.items, target)
          is MenuItem.Item -> target.add(item)
        }
      }
      return target
    }

    val errors = flatItems(items).mapNotNull { item ->
      val template = item.template
      val params = template.parameters
      when {
        params.isEmpty() || params.first().type.javaType != Directory::class.java -> {
          "first param in '${item.name}' (fun name '${template.name}') must have the 'Directory' param in first position"
        }
        params.drop(1).any { param -> param.annotations.find { it.annotationClass == Param::class } == null } -> {
          "all params (except first) in '${item.name}' (fun name '${template.name}') must have the 'Param' annotation"
        }
        params.drop(1).any { param -> param.type.javaType != String::class.java } -> {
          "all params (except first) in '${item.name}' (fun name '${template.name}') must have the only 'String' param type"
        }
        else -> null
      }
    }
    if (errors.isEmpty()) return
    error(errors.joinToString("\n"))
  }

  private fun buildMenu(group: DefaultActionGroup, menuItems: List<MenuItem>) {
    menuItems.forEach { item ->
      when (item) {
        is MenuItem.Group -> {
          val subGroup = DefaultActionGroup(item.name, true)
          group.add(subGroup, Constraints.LAST)
          buildMenu(subGroup, item.items)
        }
        is MenuItem.Item -> {
          group.add(CreateFileTemplateAnAction(item.name, item.template), Constraints.LAST)
        }
      }
    }
  }
}
