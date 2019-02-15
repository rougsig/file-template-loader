package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplate
import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.google.gson.Gson
import com.intellij.openapi.vfs.VirtualFile

fun readFileTemplates(dir: VirtualFile): List<FileTemplate> {
  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_EXTENSION) }
    .map { file ->
      val slittedName = file.nameWithoutExtension.split(FILE_NAME_DELIMITER)
      FileTemplate(
        name = slittedName.first(),
        fileName = slittedName.first(),
        extension = slittedName.last(),
        text = String(file.inputStream.readBytes())
      )
    }
}

fun readFileTemplateGroups(templates: List<FileTemplate>, dir: VirtualFile, gson: Gson): List<FileTemplateGroup> {
  val templateMap = HashMap<String, FileTemplate>()
  templates.map { templateMap["${it.name}.${it.extension}"] = it }

  return dir.fileRec
    .filter { it.name.endsWith(FILE_TEMPLATE_GROUP_EXTENSION) }
    .map { file ->
      val fileTemplateGroup = gson.fromJson(
        String(file.inputStream.readBytes()),
        FileTemplateGroupJson::class.java
      )
      FileTemplateGroup(
        name = fileTemplateGroup.name,
        templates = fileTemplateGroup.templates.map { template ->
          templateMap[template.template]!!.copy(
            fileName = template.fileName,
            directory = template.directory
          )
        }
      )
    }
}

private const val FILE_TEMPLATE_EXTENSION = "ft"
private const val FILE_TEMPLATE_GROUP_EXTENSION = "group.json"
private const val FILE_NAME_DELIMITER = "."

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }

private data class FileTemplateGroupJson(
  val name: String,
  val templates: List<FileTemplate>
) {
  data class FileTemplate(
    val template: String,
    val fileName: String,
    val directory: String?
  )
}
