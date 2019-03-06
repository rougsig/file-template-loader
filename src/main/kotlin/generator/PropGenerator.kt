package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.entity.ScopedFileTemplate

abstract class PropGenerator {
  abstract val propName: String
  abstract val requiredProps: Set<String>
  abstract val selfRequiredProps: Set<String>

  abstract fun generateProp(props: Props): String

  fun isGenerateAvailable(props: Props): Boolean {
    return requiredProps.minus(props.keys as Set<String>).isEmpty()
  }

  override fun equals(other: Any?): Boolean {
    return other == propName
  }

  override fun hashCode(): Int {
    return propName.hashCode()
  }
}

fun ScopedFileTemplate.generateProps(props: Props): Props {
  val filteredProps = Props()
  (props as Map<String, String>)
    .filterKeys { k -> requiredProps.contains(k) }
    .forEach { k, v -> filteredProps.setProperty(k, v) }

  require(requiredProps.minus(filteredProps.keys).isEmpty()) {
    throw IllegalStateException("props not found: ${requiredProps.minus(filteredProps.keys).joinToString { "$it" }}")
  }

  return scope.generateProps(filteredProps)
}
