package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplateCustomProp
import com.github.rougsig.filetemplateloader.entity.mergeTemplate

class CustomPropGenerator(
  private val prefix: String,
  private val customPropNames: Set<String>,
  private val customProp: FileTemplateCustomProp
) : PropGenerator() {
  override val propName: String = "${prefix}_${customProp.name}"

  override val requiredProps: Set<String> = customProp.requiredProps
    .mapTo(HashSet()) { propName -> if (customPropNames.contains(propName)) "${prefix}_$propName" else propName }

  override fun generateProp(props: Props): Props {
    val localScopeProps = copyPropsToLocalScopeProps(prefix, requiredProps, props)
    props.setProperty(propName, mergeTemplate(customProp.text, localScopeProps))
    return props
  }
}
