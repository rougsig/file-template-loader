package com.github.rougsig.filetemplateloader.generator

import org.apache.commons.io.output.NullWriter
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.event.EventCartridge
import org.apache.velocity.app.event.ReferenceInsertionEventHandler

fun extractProps(text: String): Set<String> {
  return text.getReferences()
    .map {
      it.replace("{", "")
        .replace("}", "")
        .replace("\$", "")
    }
    .toSet()
}

fun Set<String>.filterProps(props: Props): Set<String> {
  return this.minus(props.keys as Set<String>)
}

private fun String.getReferences(): Set<String> {
  val names = HashSet<String>()
  val velocityContext = VelocityContext()
  val ec = EventCartridge()
  ec.addEventHandler(ReferenceInsertionEventHandler { reference, _ -> names.add(reference) })
  ec.attachToContext(velocityContext)
  Velocity.evaluate(velocityContext, NullWriter.NULL_WRITER, "", this)
  return names
}

fun Set<String>.extractModificatorPropGenerators(
  prefix: String,
  customProps: Set<String>,
  initialProps: Set<String>
): List<ModificatorPropGenerator> {
  fun String.extractPropBase(
    prefix: String,
    customProps: Set<String>,
    initialProps: Set<String>
  ): Pair<String, String>? {
    return PROP_MODIFICATOR_MATCHER.find(this)?.value?.let {
      val propBase = PROP_MODIFICATOR_MATCHER.replace(this) { "" }
      val isCustomProp = customProps.contains(propBase) || initialProps.contains("${prefix}_$propBase")
      if (isCustomProp) "${prefix}_$this" to "${prefix}_$propBase" else this to propBase
    }
  }

  return mapNotNull { propName ->
    propName.extractPropBase(prefix, customProps, initialProps)?.let { (prefixedPropName, propBaseName) ->
      val modificatorName = PROP_MODIFICATOR_MATCHER.find(propName)!!.value.removePrefix("_")
      val modificator = PROP_MODIFICATORS.getValue(modificatorName)
      ModificatorPropGenerator(
        prefixedPropName,
        propBaseName,
        modificator
      )
    }
  }
}

private fun Iterable<PropGenerator>.findPropGenerator(prefix: String, propName: String): PropGenerator? {
  return find { it.propName == propName || it.propName == "${prefix}_$propName" }
}

fun Set<String>.minusGeneratedProps(
  prefix: String,
  propGenerators: List<PropGenerator>
): Set<String> {
  return this
    .flatMapTo(HashSet<String>()) { propName ->
      propGenerators.findPropGenerator(prefix, propName)?.requiredProps ?: setOf(propName)
    }
    .flatMapTo(HashSet<String>()) { propName ->
      propGenerators.findPropGenerator(prefix, propName)?.let { propGenerator ->
        val isSelfRequired = propGenerator.requiredProps.contains(propName)
        if (isSelfRequired) {
          setOf(propName)
        } else {
          emptySet()
        }
      } ?: setOf(propName)
    }
    .toSet()
}
