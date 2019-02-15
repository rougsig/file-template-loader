package com.github.rougsig.filetemplateloader.extension

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory

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

fun Project.createPsiFile(fileName: String, text: String): PsiFile {
  val type = FileTypeRegistry.getInstance().getFileTypeByFileName(fileName)
  return PsiFileFactory.getInstance(this).createFileFromText(fileName, type, text)
}
