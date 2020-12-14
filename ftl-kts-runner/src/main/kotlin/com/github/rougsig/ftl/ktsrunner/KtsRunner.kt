package com.github.rougsig.ftl.ktsrunner

import org.jetbrains.kotlin.mainKts.jsr223.KotlinJsr223MainKtsScriptEngineFactory
import javax.script.Invocable
import javax.script.ScriptEngineManager

class KtsRunner {
  private val factory = KotlinJsr223MainKtsScriptEngineFactory()
  private val engine = factory.scriptEngine

  fun compile(script: String) {
    engine.eval(script)
  }

  fun invokeFunction(name: String, vararg args: Any) {
    (this.engine as Invocable).invokeFunction(name, *args)
  }

  fun <T> invokeFunction(name: String, vararg args: Any): T {
    val result = (this.engine as Invocable).invokeFunction(name, *args)
    return result as T
  }
}
