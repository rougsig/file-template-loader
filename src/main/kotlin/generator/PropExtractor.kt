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

fun String.extractPropBaseName(): String? {
  return PROP_MODIFICATOR_MATCHER.replace(this) { "" }
    .takeIf { PROP_MODIFICATOR_MATCHER.containsMatchIn(this) }
}

fun Set<String>.extractPropBaseNames(): Set<String> {
  return mapNotNullTo(HashSet()) { it.extractPropBaseName() }
}

fun Set<String>.extractModificatorPropGenerators(): List<ModificatorPropGenerator> {
  return mapNotNull { propName ->
    propName.extractPropBaseName()?.let { propBaseName ->
      val modificatorName = PROP_MODIFICATOR_MATCHER.find(propName)!!.value.removePrefix("_")
      val modificator = PROP_MODIFICATORS.getValue(modificatorName)
      ModificatorPropGenerator(propName, propBaseName, modificator)
    }
  }
}

private fun Iterable<PropGenerator>.findPropGenerator(propName: String): PropGenerator? {
  return find { it.propName == propName }
}

fun Set<String>.applyPropGenerators(
  propGenerators: Set<PropGenerator>
): Set<String> {
  return this
    .flatMapTo(HashSet<String>()) { propGenerators.findPropGenerator(it)?.requiredProps ?: setOf(it) }
    .flatMapTo(HashSet<String>()) { propGenerators.findPropGenerator(it)?.selfRequiredProps ?: setOf(it) }
    .toSet()
}
