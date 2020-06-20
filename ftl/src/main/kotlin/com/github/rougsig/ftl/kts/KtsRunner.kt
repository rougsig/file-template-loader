package com.github.rougsig.ftl.kts

import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.io.toVirtualFile
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import javax.script.Invocable

internal class KtsRunner {
  private val ktsEngineFactory = FtlScriptEngineFactory()
  private val engine = ktsEngineFactory.scriptEngine

  fun compile(script: String) {
    validateScript(script)
    engine.eval(script)
  }

  inline fun <reified T> invokeFunction(name: String, vararg args: Any): T {
    val result = (engine as Invocable).invokeFunction(name, *args)
    return if (Unit is T) Unit
    else result as T
  }

  private fun validateScript(script: String) {
    val hasPackage = script.lines().any { it.startsWith("package") }
    if (hasPackage) error("Script shouldn't have package")
  }
}

internal fun KtsRunner.compile(templateDir: Directory) {
  val templateFiles = mutableMapOf<String, KotlinScriptFile>()
  templateDir.toVirtualFile().readTemplate("${templateDir.path}/", FileDocumentManager.getInstance(), templateFiles)
  validateIncludes(templateFiles)
  while (templateFiles.isNotEmpty()) {
    val files = templateFiles.values.filter { it.includes.isEmpty() }
    files.forEach { f ->
      templateFiles.remove(f.name)
      templateFiles.values.forEach { it.includes.remove(f.name) }
      compile(f.text)
    }
    if (files.isEmpty() && templateFiles.isNotEmpty()) {
      error("Circular dependency detected in files: ${templateFiles.values.joinToString() { it.name }}")
    }
  }
}

private fun validateIncludes(templateFiles: Map<String, KotlinScriptFile>) {
  val allIncludes = templateFiles.values.map { it.includes }.flatten().toSet()
  val includesNotFound = allIncludes.minus(templateFiles.keys)
  if (includesNotFound.isNotEmpty()) error(includesNotFound.joinToString("\n") { "Include not found: $it" })
}

private fun VirtualFile.readTemplate(
  templateDirPath: String,
  documentManager: FileDocumentManager,
  templates: MutableMap<String, KotlinScriptFile>
) {
  if (this.isDirectory) this.children.forEach { it.readTemplate(templateDirPath, documentManager, templates) }
  else {
    val name = this.path.removePrefix(templateDirPath)
    val text = documentManager.getDocument(this)?.text ?: ""
    val includes = text.lines()
      .filter { it.startsWith("@file:Include") }
      .map { it.substring(it.indexOfFirst { c -> c == '"' } + 1, it.indexOfLast { c -> c == '"' }) }
      .toMutableList()
    templates[name] = KotlinScriptFile(name = name, text = text, includes = includes, virtualFile = this)
  }
}
