package com.github.rougsig.ftl

import org.assertj.core.api.Assertions
import org.testng.annotations.Test
import javax.script.ScriptEngineManager

object Deps

class MyTest : Assertions() {
  @Test
  fun a() {
    val engine = ScriptEngineManager().getEngineByExtension("kts")
    val r = kotlin
      .runCatching {
        engine.eval(
          """
          |import com.github.rougsig.ftl.Deps
          |
          |
          |10 + 5 
          """.trimMargin()) }
      .getOrElse { throw RuntimeException("Cannot load script", it) }
      .castOrError<Int>()

    assertThat(r)
      .isEqualTo(15)
  }


  private inline fun <reified T> Any?.castOrError() = takeIf { it is T }?.let { it as T }
    ?: error("Cannot cast $this to expected type ${T::class}")
}
