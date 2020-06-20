package com.github.rougsig.ftl

import com.github.rougsig.ftl.dsl.FtlMenuBuilder
import com.github.rougsig.ftl.dsl.MenuItem
import com.github.rougsig.ftl.extenstion.ftlTemplateDir
import com.github.rougsig.ftl.extenstion.writeAction
import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.kts.KtsRunner
import com.github.rougsig.ftl.kts.compile
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaType

class FtlProjectModule(private val project: Project) {

  companion object {
    fun getInstance(project: Project): FtlProjectModule {
      return ServiceManager.getService(project, FtlProjectModule::class.java)
    }
  }

  private val ftlGroup = DefaultActionGroup("From FTL", true)

  init {
    project.writeAction { createFtlModule(project) }

    val createNewGroup = ActionManager.getInstance().getAction("NewGroup") as DefaultActionGroup
    createNewGroup.add(ftlGroup, Constraints.FIRST)
    reloadTemplates()
  }

  fun reloadTemplates(silent: Boolean = true) {
    try {
      val templates = project.ftlTemplateDir
      val runner = KtsRunner()
      runner.compile(templates)

      val menuBuilder = FtlMenuBuilder()
      runner.invokeFunction<Unit>("buildMenu", menuBuilder)
      val items = menuBuilder.build()
      validateMenuItems(items)

      ftlGroup.removeAll()
      buildMenu(ftlGroup, items)
      if (!silent) Messages.showInfoMessage("Reloaded Successfully", "File Templates")
    } catch (e: Exception) {
      if (!silent) Messages.showErrorDialog(
        "Load Failed\n${e.message}\n${e.stackTrace.toList().joinToString("\n")}",
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
          group.add(CreateTemplateAction(item.name, item.template), Constraints.LAST)
        }
      }
    }
  }

  private class CreateTemplateAction(
    private val name: String,
    private val template: KFunction<String>
  ) : AnAction(name) {
    override fun actionPerformed(e: AnActionEvent) {
      println("+100500 $this, $name, $template")
    }
  }
}
