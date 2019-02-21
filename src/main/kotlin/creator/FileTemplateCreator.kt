package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.entity.Props
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

interface FileTemplateCreator {
  fun create(dir: PsiDirectory, props: Props): List<PsiFile>
}
