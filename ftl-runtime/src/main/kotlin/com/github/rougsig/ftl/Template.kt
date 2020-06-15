package com.github.rougsig.ftl

import kotlin.reflect.KFunction

data class Template(
  val name: String,
  val func: KFunction<String>
)
