package com.github.rougsig.ftl.kts

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import javax.script.Bindings
import javax.script.ScriptContext
import kotlin.script.experimental.jvm.util.classpathFromClassloader

internal class FtlKotlinJsr223JvmLocalScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {
  override fun getScriptEngine() = KotlinJsr223JvmLocalScriptEngine(
    this,
    classpathFromClassloader(this::class.java.classLoader, unpackJarCollections = true)!!,
    KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
    { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
    arrayOf(Bindings::class)
  )
}
