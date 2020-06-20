package com.github.rougsig.ftl.extenstion

import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.io.DirectoryImpl
import com.intellij.openapi.module.Module
import org.jetbrains.kotlin.idea.util.rootManager

internal fun Module.findModuleRoot(): Directory? {
  return this.moduleFile?.parent?.let { DirectoryImpl.find(project, it) }
}

internal fun Module.findSourceRoot(): Directory? {
  return this.rootManager.contentRoots.firstOrNull()?.let { DirectoryImpl.find(project, it) }
}
