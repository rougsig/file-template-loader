package com.github.rougsig.ftl

import com.github.rougsig.ftl.io.DirectoryImpl
import com.github.rougsig.ftl.io.toVirtualFile
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleTypeId
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager

internal const val MODULE_DIR_NAME = "ftl"
internal const val LIB_DIR_NAME = "lib"
internal const val TEMPLATE_DIR_NAME = "template"

internal fun createFtlModule(project: Project) {
  val basePath = project.basePath ?: error("project baseDir is null")
  val baseDir = DirectoryImpl.find(project, basePath)
  val moduleDir = baseDir.createDirectory(MODULE_DIR_NAME)
  val moduleFile = moduleDir.createFile("$MODULE_DIR_NAME.iml")
  val libDir = moduleDir.createDirectory(LIB_DIR_NAME)
  val templateDir = moduleDir.createDirectory(TEMPLATE_DIR_NAME)
}
