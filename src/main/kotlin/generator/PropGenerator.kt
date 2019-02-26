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
  val avaliableGenerators = propGenerators.filter { it.isGenerateAvailable(props) }
  return props
}
