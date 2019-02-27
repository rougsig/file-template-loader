package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.toDotCase

class PackageNamePropGenerator(
  prefix: String,
  private val directory: String?
) : PropGenerator() {
  override val propName: String = "${prefix}_$PROP_PACKAGE_NAME"

  override val requiredProps: Set<String> = setOf(PROP_PACKAGE_NAME)

  override fun generateProp(props: Props) {
    val packageNameBuilder = StringBuilder()
    packageNameBuilder.append(props.getProperty(PROP_PACKAGE_NAME) ?: "")
    if (directory != null && directory.isNotBlank()) {
      packageNameBuilder.append(".")
      packageNameBuilder.append(directory.toDotCase())
    }

    props.setProperty(propName, packageNameBuilder.toString())
  }
}
