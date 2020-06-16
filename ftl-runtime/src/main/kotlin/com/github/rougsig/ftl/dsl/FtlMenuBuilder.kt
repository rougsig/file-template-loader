package com.github.rougsig.ftl.dsl

import kotlin.reflect.KFunction

class FtlMenuBuilder {
  private val items = mutableListOf<Pair<String, Any>>()

  fun item(name: String, template: KFunction<String>) {
    this.items.add(name to template)
  }

  fun group(name: String, init: FtlMenuBuilder.() -> Unit) {
    this.items.add(name to FtlMenuBuilder().also(init).build())
  }

  fun build(): List<Pair<String, Any>> {
    return items
  }
}
