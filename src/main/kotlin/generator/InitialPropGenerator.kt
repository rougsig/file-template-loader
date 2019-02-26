package com.github.rougsig.filetemplateloader.generator

class InitialPropGenerator(
  override val propName: String,
  private val generator: (Props) -> String
) : PropGenerator() {
  override val requiredProps: Set<String> = emptySet()

  override fun generateProp(props: Props) {
    props.setProperty(propName, generator(props))
  }
}
