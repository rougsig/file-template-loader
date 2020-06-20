package com.github.rougsig.ftl.extenstion

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.idea.util.projectStructure.allModules

internal fun VirtualFile.findModule(project: Project): Module? {
  return project.allModules().find { it.moduleFilePath.contains(this.path) }
}
