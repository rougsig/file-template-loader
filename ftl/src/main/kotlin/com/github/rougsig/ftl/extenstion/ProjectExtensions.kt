package com.github.rougsig.ftl.extenstion

import com.github.rougsig.ftl.LIB_DIR_NAME
import com.github.rougsig.ftl.MODULE_DIR_NAME
import com.github.rougsig.ftl.TEMPLATE_DIR_NAME
import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.io.DirectoryImpl
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

internal fun <R : Any> Project.writeAction(action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .compute<R, Exception>(action)
}

internal val Project.ftlModuleDir: Directory
  get() {
    val basePath = this.basePath ?: error("project baseDir is null")
    return DirectoryImpl.find(basePath).createDirectory(MODULE_DIR_NAME)
  }

internal val Project.ftlTemplateDir: Directory
  get() = ftlModuleDir.createDirectory(TEMPLATE_DIR_NAME)

internal val Project.ftlLibDir: Directory
  get() = ftlModuleDir.createDirectory(LIB_DIR_NAME)
