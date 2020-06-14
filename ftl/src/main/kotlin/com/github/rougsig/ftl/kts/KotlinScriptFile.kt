package com.github.rougsig.ftl.kts

import com.intellij.openapi.vfs.VirtualFile

internal data class KotlinScriptFile(
  val name: String,
  val text: String,
  val virtualFile: VirtualFile,
  val includes: MutableList<String>
)
