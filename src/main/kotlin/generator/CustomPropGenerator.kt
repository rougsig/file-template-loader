package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplateCustomProp
import com.github.rougsig.filetemplateloader.entity.mergeTemplate

class CustomPropGenerator(
  private val customProp: FileTemplateCustomProp
) : PropGenerator() {
  override val propName: String
    get() = customProp.name

  override val requiredProps: Set<String>
    get() = customProp.requiredProps

  override val selfRequiredProps: Set<String> = {
    if (requiredProps.contains(customProp.name)) setOf(customProp.name)
    else emptySet()
  }()

  override fun generateProp(props: Props): String {
    return mergeTemplate(customProp.text, props)
  }
}
