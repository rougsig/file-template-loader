package com.github.rougsig.ftl.kts

import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmSdkRoots
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingCompilerConfigurationComponentRegistrar
import org.jetbrains.kotlin.scripting.compiler.plugin.repl.GenericReplCompiler
import org.jetbrains.kotlin.scripting.definitions.KotlinScriptDefinition
import org.jetbrains.kotlin.scripting.resolve.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import kotlin.reflect.KClass
import kotlin.script.experimental.jvm.util.classpathFromClassloader

internal class FtlScriptEngineFactory : KotlinJsr223JvmScriptEngineFactoryBase() {
  override fun getScriptEngine(): ScriptEngine = FtlScriptEngine(
    this,
    { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
    arrayOf(Bindings::class)
  )
}

private class FtlScriptEngine(
  factory: ScriptEngineFactory,
  val getScriptArgs: (ScriptContext, Array<out KClass<out Any>>?) -> ScriptArgsWithTypes?,
  val scriptArgsTypes: Array<out KClass<out Any>>?
) : KotlinJsr223JvmScriptEngineBase(factory), KotlinJsr223JvmInvocableScriptEngine {
  private val classLoader = this::class.java.classLoader

  override val replCompiler: ReplCompiler by lazy {
    GenericReplCompiler(
      makeScriptDefinition(emptyList(), KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!),
      makeCompilerConfiguration(),
      PrintingMessageCollector(System.out, MessageRenderer.WITHOUT_PATHS, false)
    )
  }

  private val localEvaluator by lazy {
    GenericReplCompilingEvaluator(replCompiler, emptyList(), classLoader, getScriptArgs(getContext(), scriptArgsTypes))
  }

  private fun makeScriptDefinition(templateClasspath: List<File>, templateClassName: String): KotlinScriptDefinition {
    val classloader = URLClassLoader(templateClasspath.map { it.toURI().toURL() }.toTypedArray(), classLoader)
    val cls = classloader.loadClass(templateClassName)
    return KotlinScriptDefinitionFromAnnotatedTemplate(cls.kotlin, emptyMap())
  }

  override val replEvaluator: ReplFullEvaluator
    get() = localEvaluator

  override val state: IReplStageState<*>
    get() = getCurrentState(getContext())

  override fun createState(lock: ReentrantReadWriteLock): IReplStageState<*> {
    return replEvaluator.createState(lock)
  }

  override fun overrideScriptArgs(context: ScriptContext): ScriptArgsWithTypes? {
    return getScriptArgs(context, scriptArgsTypes)
  }

  private fun makeCompilerConfiguration() = CompilerConfiguration().apply {
    addJvmSdkRoots(PathUtil.getJdkClassesRootsFromCurrentJre())
    addJvmClasspathRoots(classpathFromClassloader(classLoader, unpackJarCollections = true)!!)
    add(ComponentRegistrar.PLUGIN_COMPONENT_REGISTRARS, ScriptingCompilerConfigurationComponentRegistrar())
    put(CommonConfigurationKeys.MODULE_NAME, "kotlin-script")
    languageVersionSettings = LanguageVersionSettingsImpl(
      LanguageVersion.LATEST_STABLE, ApiVersion.LATEST_STABLE, mapOf(AnalysisFlags.skipMetadataVersionCheck to true)
    )
  }
}
