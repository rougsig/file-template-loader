package com.github.rougsig.ftl

import com.github.rougsig.ftl.extenstion.ftlTemplateDir
import com.github.rougsig.ftl.extenstion.writeAction
import com.github.rougsig.ftl.kts.KtsRunner
import com.github.rougsig.ftl.kts.compile
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import kotlin.reflect.KFunction

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
      // ScriptEngine uses another class loader. We hack this case with basic types.
      val items = runner.invokeFunction<List<Pair<String, Any>>>("buildMenu")
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

  private fun buildMenu(group: DefaultActionGroup, menuItems: List<Pair<String, Any>>) {
    menuItems.forEach { item ->
      when {
        item.second is List<*> -> {
          val subGroup = DefaultActionGroup(item.first, true)
          group.add(subGroup, Constraints.LAST)
          buildMenu(subGroup, item.second as List<Pair<String, Any>>)
        }
        item.second is KFunction<*> -> {
          group.add(CreateTemplateAction(item.first, item.second as KFunction<String>), Constraints.LAST)
        }
        else -> error("unknown menu item type: $item")
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
