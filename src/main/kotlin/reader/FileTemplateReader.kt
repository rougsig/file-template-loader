package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.*
import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun Project.readFileTemplates(): List<FileTemplateSingle> {
  val dir = guessProjectDir()?.findChild(FILE_TEMPLATE_FOLDER_NAME) ?: return emptyList()
  return readFileTemplates(dir)
}

fun Project.readFileTemplateGroups(
  templates: List<FileTemplateSingle>,
  gson: Gson
): List<FileTemplateGroup> {
  val dir = guessProjectDir()?.findChild(FILE_TEMPLATE_FOLDER_NAME) ?: return emptyList()
  return readFileTemplateGroups(templates, dir, gson)
}

fun Project.readFileTemplateModules(
  templates: List<FileTemplateSingle>,
  gson: Gson
): List<FileTemplateModule> {
  val dir = guessProjectDir()?.findChild(FILE_TEMPLATE_FOLDER_NAME) ?: return emptyList()
  return readFileTemplateModules(templates, dir, gson)
}

fun readFileTemplates(dir: VirtualFile): List<FileTemplateSingle> {
  println("Read FileTemplates from: $dir")

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_EXTENSION) }
    .map(::readFileTemplate)
}

fun readFileTemplate(file: VirtualFile): FileTemplateSingle {
  val slittedName = file.nameWithoutExtension.split(FILE_NAME_DELIMITER)
  return FileTemplateSingle(
    name = file.name,
    fileName = null,
    extension = slittedName.last(),
    text = String(file.inputStream.readBytes())
  )
}

fun createTemplateMap(templates: List<FileTemplateSingle>): Map<String, FileTemplateSingle> {
  val templateMap = HashMap<String, FileTemplateSingle>()
  templates.map { templateMap[it.name] = it }
  return templateMap
}

fun readFileTemplateGroups(templates: List<FileTemplateSingle>, dir: VirtualFile, gson: Gson): List<FileTemplateGroup> {
  println("Read FileTemplateGroups from: $dir")

  val templateMap = createTemplateMap(templates)

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_GROUP_EXTENSION) }
    .map { file ->
      val fileTemplateGroup = gson.fromJson(
        String(file.inputStream.readBytes()),
        FileTemplateGroupJson::class.java
      )
      val groups = fileTemplateGroup.templates.map { template ->
        templateMap[template.template]?.copy(
          fileName = template.fileName,
          directory = template.directory
        ) ?: throw KotlinNullPointerException(
          "template with name ${template.template} not found." +
              " Requested by Group: ${fileTemplateGroup.name}"
        )
      }
      val injectors = fileTemplateGroup.injectors?.map { entry ->
        FileTemplateInjector(
          text = entry.text,
          selector = entry.selector,
          className = entry.className,
          pathName = entry.pathName
        )
      } ?: emptyList()
      FileTemplateGroup(
        name = fileTemplateGroup.name,
        templates = groups,
        injectors = injectors
      )
    }
}

fun readFileTemplateModules(
  templates: List<FileTemplateSingle>,
  dir: VirtualFile,
  gson: Gson
): List<FileTemplateModule> {
  println("Read FileTemplateModules from: $dir")

  val templateMap = createTemplateMap(templates)

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_MODULE_EXTENSION) }
    .map { file ->
      val fileTemplateModule = gson.fromJson(
        String(file.inputStream.readBytes()),
        FileTemplateModuleJson::class.java
      )
      val sourceSets = fileTemplateModule.sourceSets.map { sourceSet ->
        FileTemplateSourceSet(
          directory = sourceSet.directory,
          templates = sourceSet.templates.map { template ->
            templateMap[template.template]?.copy(
              fileName = template.fileName,
              directory = template.directory
            ) ?: throw KotlinNullPointerException(
              "template with name ${template.template} not found." +
                  " Requested by Module: ${fileTemplateModule.name}"
            )
          }
        )
      }
      val injectors = fileTemplateModule.injectors?.map { entry ->
        FileTemplateInjector(
          text = entry.text,
          selector = entry.selector,
          className = entry.className,
          pathName = entry.pathName
        )
      } ?: emptyList()
      FileTemplateModule(
        name = fileTemplateModule.name,
        moduleName = fileTemplateModule.moduleName,
        sourceSets = sourceSets,
        injectors = injectors
      )
    }
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }

