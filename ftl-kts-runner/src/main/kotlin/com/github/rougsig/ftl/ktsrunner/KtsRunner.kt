package com.github.rougsig.ftl.ktsrunner

import javax.script.Invocable
import javax.script.ScriptEngineManager

class KtsRunner {
  private val engine by lazy { ScriptEngineManager().getEngineByExtension("main.kts")!! }

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
