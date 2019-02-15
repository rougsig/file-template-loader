package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PACKAGE_BASE
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.util.projectStructure.module
import java.util.*

fun PsiDirectory.calculatePackageName(props: Properties): String {
  val builder = StringBuilder()
  getKotlinFqName()
    ?.let { builder.append(it.toString()) }
    ?: { builder.append(props.getProperty(PACKAGE_BASE)) }()

  module?.name?.toPackageCase()?.let {
    builder.append(".")
    builder.append(it)
  }

  return builder.toString()
}
