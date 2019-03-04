package com.github.rougsig.filetemplateloader.scope

import com.github.rougsig.filetemplateloader.generator.PropGenerator
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.ScopedPropGenerator

class PropScope(
  val name: String,
  @Transient val propGenerators: Set<PropGenerator>,
  @Transient val childScopes: Set<PropScope>
) {
  val scopedPropGenerators: Set<ScopedPropGenerator> = propGenerators
    .map {
      ScopedPropGenerator(
        this,
        it
      )
    }
    .plus(childScopes.flatMap { childScope ->
      childScope.propGenerators.map {
        ScopedPropGenerator(
          childScope,
          it
        )
      }
    })
    .toSet()

  fun copyPropsToLocalScope(props: Props): Props {
    val propGeneratorNames = propGenerators.map(PropGenerator::propName)
    val scopedPropGeneratorNames = scopedPropGenerators.map(PropGenerator::propName)

    val localScopeProps = Props()
    (props as Map<String, String>)
      .toList()
      .sortedBy { (k, _) -> if (scopedPropGeneratorNames.contains(k)) 1 else -1 }
      .forEach { (k, v) ->
        val noPrefixPropName = k.removePrefix("${name}_")
        if (scopedPropGeneratorNames.contains(k) && propGeneratorNames.contains(noPrefixPropName)) {
          localScopeProps.setProperty(noPrefixPropName, v)
        } else {
          localScopeProps.setProperty(k, v)
        }
      }
    return localScopeProps
  }

  private fun Set<ScopedPropGenerator>.generateProps(props: Props): Props {
    val canBeGenerated = filter { it.isGenerateAvailable(props) }

    if (canBeGenerated.isEmpty() && isNotEmpty())
      throw IllegalStateException("can't generate props: ${joinToString { it.propName }}")

    canBeGenerated.forEach { props.setProperty(it.propName, it.generateProp(props)) }
    if (isNotEmpty()) minus(canBeGenerated).generateProps(props)

    return props
  }

  fun generateProps(props: Props): Props {
    return scopedPropGenerators.generateProps(props)
  }

  override fun equals(other: Any?): Boolean {
    return other == name
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }
}
