package com.github.rougsig.filetemplateloader.extension

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project

fun <R : Any> Project.writeAction(name: String?, action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .withName(name)
    .compute<R, Exception>(action)
}

fun <R : Any> Project.writeAction(action: () -> R): R {
  return WriteCommandAction
    .writeCommandAction(this)
    .compute<R, Exception>(action)
}
