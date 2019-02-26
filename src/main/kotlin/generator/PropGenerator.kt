package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.extension.*
import java.util.*

typealias Props = Properties
typealias PropModificators = Map<String, (String) -> String>

val PROP_MODIFICATORS: PropModificators = HashMap<String, (String) -> String>().apply {
  put("LOWER_CAMEL_CASE", String::toLowerCamelCase)
  put("UPPER_CAMEL_CASE", String::toUpperCamelCase)
  put("LOWER_SNAKE_CASE", String::toLowerSnakeCase)
  put("UPPER_SNAKE_CASE", String::toUpperSnakeCase)
  put("LOWER_KEBAB_CASE", String::toLowerKebabCase)
  put("UPPER_KEBAB_CASE", String::toUpperKebabCase)
  put("DOT_CASE", String::toDotCase)
  put("SOLID_CASE", String::toSolidCase)
  put("UPPER_CASE", String::toUpperCase)
  put("LOWER_CASE", String::toLowerCase)
}

val GENERATED_PROP_MATCHER = PROP_MODIFICATORS.keys.joinToString("|") { "_$it" }.toRegex()

fun Set<String>.generateProps(props: Props): Props {
  filter { GENERATED_PROP_MATCHER.containsMatchIn(it) }
    .map { fullPropName ->
      val generatorName = GENERATED_PROP_MATCHER.find(fullPropName)!!.value.removePrefix("_")
      val propBase = fullPropName.extractPropBase()

      val propGenerator = PROP_MODIFICATORS.getValue(generatorName)
      val generatedProp = propGenerator(props.getProperty(propBase))

      props.setProperty(fullPropName, generatedProp)
    }

  return props
}
