package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplateCustomProp
import com.github.rougsig.filetemplateloader.extension.mergeTemplate

class CustomPropGenerator(
  private val customProp: FileTemplateCustomProp
) : PropGenerator() {
  override val propName: String
    get() = customProp.name

  override val requiredProps: Set<String>
    get() = customProp.requiredProps

  override val selfRequiredProps: Set<String> = {
    val requiredPropBaseNames = requiredProps.plus(requiredProps.extractPropBaseNames())
    if (requiredPropBaseNames.contains(customProp.name)) setOf(customProp.name)
    else emptySet()
  }()

  override fun generateProp(props: Props): String {
    return mergeTemplate(customProp.text, props)
  }
}
