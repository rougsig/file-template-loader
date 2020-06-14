package com.github.rougsig.ftl.kts

import java.io.InputStream
import java.io.Reader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import java.io.File as JavaFile

internal class KtsRunner(private val templateClasspath: List<JavaFile>) {
  private val ktsEngineFactory = FtlKotlinJsr223JvmLocalScriptEngineFactory(templateClasspath)
  private val scriptEngineManager = ScriptEngineManager()
    .apply { registerEngineExtension("kts", ktsEngineFactory) }
  private val engine: ScriptEngine = scriptEngineManager.getEngineByExtension("kts")

  fun load(script: String) {
    engine.eval(script)
  }

  fun load(reader: Reader) {
    engine.eval(reader)
  }

  fun load(inputStream: InputStream) {
    return load(inputStream.reader())
  }

  fun loadAll(vararg inputStream: InputStream) {
    return inputStream.forEach(::load)
  }

  fun invokeFunction(name: String, vararg args: Any): Any? {
    return (engine as Invocable).invokeFunction(name, *args)
  }
}
