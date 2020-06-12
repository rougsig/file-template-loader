package com.github.rougsig.ftl

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.BufferedInputStream
import java.io.File

class FtlProjectModule(private val project: Project) {

  companion object {
    fun getInstance(project: Project): FtlProjectModule {
      return ServiceManager.getService(project, FtlProjectModule::class.java)
    }
  }

  init {

    val basePath = project.basePath ?: error("wtf basePath is null")
    val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: error("wtf baseDir is null")
    val name = "ftl"
    val moduleDir = baseDir.findOrCreateChildDirectory(name)
    val m = project.edit { mm ->
      mm.newModule(moduleDir.findOrCreateChild(name + ".iml").path, ModuleTypeId.JAVA_MODULE)
    }
    val libDir = moduleDir.findOrCreateChildDirectory("lib")
    extractLibs(libDir)
    project.writeAction {
      val rm = m.rootManager.modifiableModel
      rm.inheritSdk()
      val src = moduleDir.findOrCreateChildDirectory("template")
      rm.addContentEntry(src).addSourceFolder(src, false)
      rm.commit()
    }
    val lib = project.writeAction {
      val tables = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
      tables.libraries.find { it.name == "ftl" }?.let {
        tables.modifiableModel.apply {
          removeLibrary(it)
          commit()
        }
      }
      tables.libraries
        .find { it.name?.contains("kotlin-stdlib:") == true }
        ?.let { ModuleRootModificationUtil.addDependency(m, it) }
      val ftlLib = tables.createLibrary("ftl")
      ftlLib.modifiableModel.apply {
        addRoot(libDir.path + "/ide-runtime-2.0.0.jar", OrderRootType.CLASSES)
        commit()
      }
      ftlLib
    }
    ModuleRootModificationUtil.addDependency(m, lib)
  }
}

private fun extractLibs(target: VirtualFile): List<String> {
  val extractedLibs = mutableListOf<String>()
  val l = FtlProjectModule::class.java.classLoader.getResourceAsStream("ide-runtime-2.0.0.jar")
    .also { extractedLibs.add("ide-runtime-2.0.0.jar") }
  val okioJar = BufferedInputStream(l).readBytes()

  File(target.path)
    .also {
      it.mkdirs()
    }
  File(target.path + "/" + "ide-runtime-2.0.0.jar").also {
    it.deleteOnExit()
    it.createNewFile()
    it.writeBytes(okioJar)
  }
  return extractedLibs.map { target.path + "/" + it }
}

private fun VirtualFile.findOrCreateChildDirectory(name: String): VirtualFile {
  return this.findChild(name) ?: this.createChildDirectory(null, name)
}

private fun VirtualFile.findOrCreateChild(name: String): VirtualFile {
  return this.findChild(name) ?: this.createChildData(null, name)
}

private fun <R : Any> Project.writeAction(action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .compute<R, Exception>(action)
}

private fun createModule(project: Project, projectPath: String, moduleTypeId: String): Module {
  val module: Module = project.writeAction {
    ModuleManager.getInstance(project).newModule(projectPath, moduleTypeId)
  }

  val root = VfsUtil.findFileByIoFile(File(projectPath), true) ?: throw AssertionError("Can't find $projectPath")

  ModuleRootModificationUtil.updateModel(module) { t ->
    val e = t.addContentEntry(root)
    e.addSourceFolder(root, false)
  }

  return module
}

private fun <T> Project.edit(action: (ModifiableModuleModel) -> T): T {
  val model = createModifiableModuleModel(this)
  val result = action(model)
  runWriteActionAndWait { model.commit() }
  return result
}

private fun createModifiableModuleModel(project: Project): ModifiableModuleModel {
  val moduleManager = ModuleManager.getInstance(project)
  return runReadAction { moduleManager.modifiableModel }
}

inline fun <T> runWriteActionAndWait(crossinline action: () -> T): T {
  @Suppress("RemoveExplicitTypeArguments")
  return WriteAction.computeAndWait(ThrowableComputable<T, Throwable> { action() })
}
