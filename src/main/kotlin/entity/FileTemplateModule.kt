package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.constant.PROPS_MODULE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.generatePackageName
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import java.util.*

data class FileTemplateModule(
  val moduleName: String,
  override val name: String,
  val sourceSets: List<FileTemplateSourceSet>,
  val injectors: List<FileTemplateInjector>
) : FileTemplate {
  override fun create(dir: PsiDirectory, props: Properties): List<PsiFile> {
    println("Create FileTemplateModule: \n name: $name \n dir: $dir \n extractedProps: $props \n")

    val projectDir = dir.manager.findDirectory(dir.project.guessProjectDir()!!)!!
    val moduleMergedNamed = mergeTemplate(moduleName, props)
    val moduleDir = projectDir.createSubdirectory(moduleMergedNamed)

    val sourceSets = sourceSets.flatMap { it.create(moduleDir, props) }
    val injectors = injectors.flatMap { it.create(moduleDir, props) }

    return sourceSets.plus(injectors)
  }

  override fun generateProps(props: Properties) {
    val templates = sourceSets.flatMap { it.templates }
    generateProps(props, templates) { getRequiredProps(props) }

    props.setProperty(PROPS_MODULE_NAME, mergeTemplate(moduleName, props))
    props.generatePackageName()

    generateClassNameProps(props, templates, props.getProperty(PROPS_PACKAGE_NAME))
  }

  override val hasClassName: Boolean = false

  override fun getAllProps(): Set<String> {
    val templateProps = sourceSets
      .flatMap { it.getAllProps() }
      .toTypedArray()

    val entryProps = injectors
      .flatMap { it.getAllProps() }
      .toTypedArray()

    val nameProps = extractProps(moduleName).toTypedArray()

    return setOf(*templateProps, *entryProps, *nameProps)
  }
}
