package com.github.rougsig.ftl

import kotlin.reflect.KFunction

data class TemplateDeclaration(
  val name: String,
  val func: KFunction<String>
)
