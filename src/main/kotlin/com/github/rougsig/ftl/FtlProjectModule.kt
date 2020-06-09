package com.github.rougsig.ftl

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.module.impl.ModuleImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.kotlin.idea.run.asJvmModule
import java.io.File

class FtlProjectModule(private val project: Project) {
  companion object {
    fun getInstance(project: Project): FtlProjectModule {
      return ServiceManager.getService(project, FtlProjectModule::class.java)
    }
  }

  init {
    init()
  }

  fun init() {
    project.writeAction {

    }
  }
}

fun <R : Any> Project.writeAction(action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .compute<R, Exception>(action)
}
