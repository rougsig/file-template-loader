package com.github.rougsig.ftl.kts

import com.github.rougsig.ftl.FtlProjectModule
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.net.URLClassLoader
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager


internal class KtsRunner {
  private val ktsEngineFactory = KotlinJsr223JvmLocalScriptEngineFactory()
  private val classLoader = FtlProjectModule::class.java.classLoader
    .let {
      URLClassLoader(arrayOf<URL>(FtlProjectModule::class.java.classLoader.getResource("ftl-runtime.jar")!!), this.javaClass.classLoader)
  }
  private val scriptEngineManager = ScriptEngineManager(classLoader)
    .apply { registerEngineExtension("kts", ktsEngineFactory) }
  private val engine: ScriptEngine = scriptEngineManager.getEngineByExtension("kts")

  init {
//    libNames.forEach { jarName ->
//      val inputStream = FtlProjectModule::class.java.classLoader.getResourceAsStream(jarName)
//        ?: error("jar not found with name $jarName")
//      load(BufferedInputStream(inputStream))
//    }
  }

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

  fun invokeFunction(name: String, vararg args: Any) {
    (engine as Invocable).invokeFunction(name, *args)
  }
}
