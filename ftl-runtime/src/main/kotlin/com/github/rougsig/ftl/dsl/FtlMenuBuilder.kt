package com.github.rougsig.ftl.dsl

import kotlin.reflect.KFunction

sealed class MenuItem {
  abstract val name: String

  data class Group(override val name: String, val items: List<MenuItem>) : MenuItem()
  data class Item(override val name: String, val template: KFunction<String>) : MenuItem()
}

class FtlMenuBuilder {
  private val items = mutableListOf<MenuItem>()

  fun item(name: String, template: KFunction<String>) {
    this.items.add(MenuItem.Item(name, template))
  }

  fun group(name: String, init: FtlMenuBuilder.() -> Unit) {
    this.items.add(MenuItem.Group(name, FtlMenuBuilder().also(init).build()))
  }

  fun build(): List<MenuItem> {
    return items
  }
}
