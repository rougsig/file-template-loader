package com.github.rougsig.filetemplateloader.generator

class ModificatorPropGenerator(
  override val propName: String,
  private val propBaseName: String,
  private val modificator: (String) -> String
) : PropGenerator() {
  override val requiredProps: Set<String> = setOf(propBaseName)

  override fun generateProp(props: Props) {
    props.setProperty(propName, modificator(props.getProperty(propBaseName)))
  }
}
