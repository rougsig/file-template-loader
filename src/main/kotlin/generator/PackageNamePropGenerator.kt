package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.toDotCase

class PackageNamePropGenerator(
  private val directory: String?,
  prefix: String? = null
) : PropGenerator() {
  override val propName: String = prefix?.let { "${it}_$PROP_PACKAGE_NAME" } ?: PROP_PACKAGE_NAME

  override val requiredProps: Set<String> = setOf(PROP_PACKAGE_NAME)

  override fun generateProp(props: Props): Props {
    val packageNameBuilder = StringBuilder()
    packageNameBuilder.append(props.getProperty(PROP_PACKAGE_NAME) ?: "")
    if (directory != null && directory.isNotBlank()) {
      packageNameBuilder.append(".")
      packageNameBuilder.append(directory.toDotCase())
    }

    props.setProperty(propName, packageNameBuilder.toString())

    return props
  }
}
