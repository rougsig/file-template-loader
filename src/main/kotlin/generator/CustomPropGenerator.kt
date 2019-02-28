package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplateCustomProp
import com.github.rougsig.filetemplateloader.entity.mergeTemplate

class CustomPropGenerator(
  prefix: String,
  private val customProp: FileTemplateCustomProp
) : PropGenerator() {
  override val propName: String = "${prefix}_${customProp.name}"

  override val requiredProps: Set<String>
    get() = customProp.requiredProps

  override fun generateProp(props: Props): Props {
    props.setProperty(propName, mergeTemplate(customProp.text, props))
    return props
  }
}
