package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.generator.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

abstract class FileTemplate {
  abstract val extractedProps: Set<String>
  open val requiredProps: Set<String> = extractedProps

  abstract fun create(dir: PsiDirectory, props: Props): List<PsiFile>
}
