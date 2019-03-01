package com.github.rougsig.filetemplateloader.generator

class InitialPropGenerator(
  override val propName: String,
  override val requiredProps: Set<String> = emptySet(),
  private val generator: (Props) -> String
) : PropGenerator() {
  override fun generateProp(props: Props): Props {
    props.setProperty(propName, generator(props))
    return props
  }
}
