package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.FileTemplate

abstract class PropGenerator {
  abstract val propName: String
  abstract val requiredProps: Set<String>

  abstract fun generateProp(props: Props)

  fun isGenerateAvailable(props: Props): Boolean {
    return requiredProps.minus(props.keys as Set<String>).isEmpty()
  }
}

fun FileTemplate.generateProps(props: Props): Props {
  return propGenerators.generateProps(props)
}

fun List<PropGenerator>.generateProps(props: Props): Props {
  val canBeGenerated = filter { it.isGenerateAvailable(props) }

  if (canBeGenerated.isEmpty() && isNotEmpty())
    throw IllegalStateException("can't generate props: ${joinToString { it.propName }}")

  canBeGenerated.forEach { it.generateProp(props) }
  if (isNotEmpty()) minus(canBeGenerated).generateProps(props)

  return props
}
