package com.github.rougsig.filetemplateloader.generator

class ModificatorPropGenerator(
  override val propName: String,
  private val propBaseName: String,
  @Transient private val modificator: (String) -> String
) : PropGenerator() {
  override val requiredProps: Set<String> = setOf(propBaseName)

  override val selfRequiredProps: Set<String> = emptySet()

  override fun generateProp(props: Props): String {
    return modificator(props.requireProperty(propBaseName))
  }
}
