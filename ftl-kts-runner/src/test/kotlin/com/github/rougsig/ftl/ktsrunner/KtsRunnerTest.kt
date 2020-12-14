package com.github.rougsig.ftl.ktsrunner

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class KtsRunnerTest : StringSpec({
  "should invoke myFun function returns 5" {
    val runner = KtsRunner()
    runner.compile(
      """
      |fun myFun() = 5
      """.trimMargin()
    )
    runner.invokeFunction<Int>("myFun") shouldBe 5
  }

  "should include kotlinpoet returns kotlin source code from koltinpoet" {
    val runner = KtsRunner()
    runner.compile(
      """
      |@file:DependsOn("com.squareup:kotlinpoet:1.7.2")
      |
      |import com.squareup.kotlinpoet.*
      |
      |fun myFun(): String {
      |  val main = FunSpec.builder("main")
      |    .addStatement("var total = 0")
      |    .beginControlFlow("for (i in 0 until 10)")
      |    .addStatement("total += i")
      |    .endControlFlow()
      |    .build()
      |  return main.toString()  
      |}
      """.trimMargin()
    )
    runner.invokeFunction<Int>("myFun") shouldBe
      """
      |public fun main(): kotlin.Unit {
      |  var total = 0
      |  for (i in 0 until 10) {
      |    total += i
      |  }
      |}
      |
      """.trimMargin()
  }
})
