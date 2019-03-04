package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.scope.PropScope

class ScopedPropGenerator(
  @Transient private val scope: PropScope,
  private val propGenerator: PropGenerator
) : PropGenerator() {
  override val propName: String = "${scope.name}_${propGenerator.propName}"

  override val requiredProps: Set<String> =
    propGenerator.requiredProps
      .map { propName ->
        val isGeneraredProp = scope.propGenerators.find { it.propName == propName } != null
        val isSelfRequiredProp = propGenerator.selfRequiredProps.contains(propName)
        if (isGeneraredProp && !isSelfRequiredProp) "${scope.name}_${propName}"
        else propName
      }
      .toSet()

  override val selfRequiredProps: Set<String>
    get() = propGenerator.selfRequiredProps

  override fun generateProp(props: Props): String {
    return propGenerator.generateProp(scope.copyPropsToLocalScope(props))
  }
}
