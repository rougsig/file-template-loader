package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.constant.PROP_INITIAL_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.toDotCase

fun generatePackageName(props: Props, relativePath: String?): String {
  val packageNameBuilder = StringBuilder()
  packageNameBuilder.append(props.getProperty(PROP_INITIAL_PACKAGE_NAME) ?: "")
  if (relativePath != null && relativePath.isNotBlank()) {
    packageNameBuilder.append(".")
    packageNameBuilder.append(relativePath.toDotCase())
  }

  return packageNameBuilder.toString()
}
