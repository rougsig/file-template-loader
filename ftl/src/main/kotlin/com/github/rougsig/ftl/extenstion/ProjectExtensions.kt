package com.github.rougsig.ftl.extenstion

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

internal fun <R : Any> Project.writeAction(action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .compute<R, Exception>(action)
}
