package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
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
      val entries = emptyList<FileTemplateSingle>()
      FileTemplateGroup(
        name = fileTemplateGroup.name,
        templates = groups.plus(entries)
      )
    }
}

const val FILE_TEMPLATE_FOLDER_NAME = ".fileTemplates"
const val FILE_TEMPLATE_EXTENSION = "ft"
const val FILE_TEMPLATE_GROUP_EXTENSION = "group.json"
const val FILE_NAME_DELIMITER = "."

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }

private data class FileTemplateGroupJson(
  val name: String,
  val templates: List<FileTemplate>,
  val entries: List<FileTemplateEntry>
) {
  data class FileTemplate(
    val template: String,
    val fileName: String,
    val directory: String?
  )

  data class FileTemplateEntry(
    val template: String,
    val insertTo: String,
    val query: String,
    val elementSelector: String,
    val append: String
  )
}
