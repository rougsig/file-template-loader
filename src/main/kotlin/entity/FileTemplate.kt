package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

abstract class FileTemplate {
  abstract val extractedProps: Set<String>
  abstract val requiredProps: Set<String>

  abstract fun create(dir: PsiDirectory, props: Props): List<PsiFile>
}
