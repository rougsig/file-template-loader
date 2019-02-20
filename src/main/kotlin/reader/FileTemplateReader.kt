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
    .map { file ->
      val slittedName = file.nameWithoutExtension.split(FILE_NAME_DELIMITER)
      FileTemplateSingle(
        name = slittedName.first(),
        fileName = slittedName.first(),
        extension = slittedName.last(),
        text = String(file.inputStream.readBytes())
      )
    }
}

fun readFileTemplateGroups(templates: List<FileTemplateSingle>, dir: VirtualFile, gson: Gson): List<FileTemplateGroup> {
  println("Read FileTemplateGroups from: $dir")

  val templateMap = HashMap<String, FileTemplateSingle>()
  templates.map { templateMap["${it.name}.${it.extension}"] = it }

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_GROUP_EXTENSION) }
    .map { file ->
      val fileTemplateGroup = gson.fromJson(
        String(file.inputStream.readBytes()),
        FileTemplateGroupJson::class.java
      )
      val groups = fileTemplateGroup.templates.map { template ->
        templateMap[template.template]!!.copy(
          fileName = template.fileName,
          directory = template.directory
        )
      }
      val entries = fileTemplateGroup.entries.map { entry ->
        FileTemplateEntry(
          text = entry.text,
          selector = entry.selector,
          className = entry.className,
          pathName = entry.pathName
        )
      }
      FileTemplateGroup(
        name = fileTemplateGroup.name,
        templates = groups,
        entries = entries
      )
    }
}

fun readFileTemplateModules(
  templates: List<FileTemplateSingle>,
  dir: VirtualFile,
  gson: Gson
): List<FileTemplateModule> {
  println("Read FileTemplateModules from: $dir")

  val templateMap = HashMap<String, FileTemplateSingle>()
  templates.map { templateMap["${it.name}.${it.extension}"] = it }

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_MODULE_EXTENSION) }
    .map { file ->
      val fileTemplateModule = gson.fromJson(
        String(file.inputStream.readBytes()),
        FileTemplateModuleJson::class.java
      )
      val folders = fileTemplateModule.folders.flatMap { folder ->
        folder.templates.map { template ->
          templateMap[template.template]!!.copy(
            fileName = template.fileName,
            directory = template.directory?.let { "${folder.pathName}/$it" } ?: folder.pathName
          )
        }
      }
      val entries = fileTemplateModule.entries.map { entry ->
        FileTemplateEntry(
          text = entry.text,
          selector = entry.selector,
          className = entry.className,
          pathName = entry.pathName
        )
      }
      FileTemplateModule(
        name = fileTemplateModule.name,
        moduleName = fileTemplateModule.moduleName,
        group = FileTemplateGroup(
          name = "Module(${fileTemplateModule.name})",
          templates = folders,
          entries = entries
        )
      )
    }
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }

