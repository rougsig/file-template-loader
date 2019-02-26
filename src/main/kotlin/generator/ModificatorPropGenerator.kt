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

fun Set<String>.extractModificatorPropGenerators(): List<ModificatorPropGenerator> {
  return mapNotNull { propName ->
    propName.extractPropBase()?.let { propBaseName ->
      val modificatorName = PROP_MODIFICATOR_MATCHER.find(propName)!!.value.removePrefix("_")
      val modificator = PROP_MODIFICATORS.getValue(modificatorName)
      ModificatorPropGenerator(
        propName,
        propBaseName,
        modificator
      )
    }
  }
}
