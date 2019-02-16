package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.*
import com.github.rougsig.filetemplateloader.extension.mergeTemplate
import java.util.*

data class PropGenerator(
  val name: String,
  private val generator: (String) -> String
) {
  fun addGenerated(props: Properties, key: String, value: String) {
    println("Generate props $name: $key: $value")
    props.setProperty(key + "_" + name, generator(value.mergeTemplate(props)))
    println("Generated props $name: $key: ${generator(value.mergeTemplate(props))}")
    println()
  }

  fun generate(str: String) = generator(str)
}

fun Properties.generateProps(template: FileTemplate) {
  val fileName = getProperty(PROPS_NAME)
  setProperty(PROPS_SIMPLE_NAME(template.name), fileName)

  @Suppress("UNCHECKED_CAST")
  (this as Map<String, String>)
    .filterNot { (_, value) ->
      PROPS_GENERATORS.any { prop -> value.endsWith(prop.name) }
    }
    .forEach { (key, value) -> PROPS_GENERATORS.forEach { it.addGenerated(this, key, value) } }

  val packageName = getProperty(PROPS_PACKAGE_NAME)
  setProperty(PROPS_CLASS_NAME(template.name), "$packageName.$fileName")
}
