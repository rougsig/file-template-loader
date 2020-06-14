package com.github.rougsig.ftl.kts

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import javax.script.Bindings
import javax.script.ScriptContext
import java.io.File as JavaFile

internal class FtlKotlinJsr223JvmLocalScriptEngineFactory(private val templateClasspath: List<JavaFile>) :
  KotlinJsr223JvmScriptEngineFactoryBase() {

  init {

  }

  override fun getScriptEngine() = KotlinJsr223JvmLocalScriptEngine(
    this,
    templateClasspath,
    KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
    { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
    arrayOf(Bindings::class)
  )
}
