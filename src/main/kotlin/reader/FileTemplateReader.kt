package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.vfs.VirtualFile

fun createGson(): Gson {
  return GsonBuilder()
    .registerTypeAdapter(FileTemplateSingle::class.java, FileTemplateSingleAdapter())
    .registerTypeAdapter(FileTemplateGroup::class.java, FileTemplateGroupAdapter())
    .create()
}

private val gson = createGson()

fun VirtualFile.readFileTemplate(templateName: String): FileTemplate {
  return readFileTemplate(templateName, fileRec.map { it.name to it }.toMap(), emptySet())
}

fun readFileTemplate(
  templateName: String,
  templateFiles: Map<String, VirtualFile>,
  customProps: Set<FileTemplateCustomProp>
): FileTemplate {
  val templateFile = templateFiles[templateName]
    ?: throw IllegalStateException("template not found: $templateName")

  val templateNameWithoutExtension = FILE_TEMPLATE_EXTENSION_MATCHER.replace(templateName) { "" }

  return when {
    templateName.endsWith("$FILE_NAME_DELIMITER$FILE_TEMPLATE_TEMPLATE_EXTENSION") ->
      readTemplateFileTemplate(templateFile, templateFiles)

    templateName.endsWith("$FILE_NAME_DELIMITER$FILE_TEMPLATE_GROUP_EXTENSION") ->
      readGroupFileTemplate(templateFile, templateNameWithoutExtension, templateFiles, customProps)

    else -> readSingleFileTemplate(templateFile, templateNameWithoutExtension, customProps)
  }
}

private fun readGroupFileTemplate(
  file: VirtualFile,
  templateName: String,
  templateFiles: Map<String, VirtualFile>,
  customProps: Set<FileTemplateCustomProp>
): FileTemplateGroup {
  val json = gson.fromJson(String(file.inputStream.readBytes()), FileTemplateGroupJson::class.java)
  return FileTemplateGroup(
    name = json.name ?: templateName,
    templates = json.templates.map { it.toFileTemplate(templateFiles) },
    customProps = customProps.plus(json.customProps.toFileTemplateCustomProps())
  )
}

private fun readTemplateFileTemplate(
  file: VirtualFile,
  templateFiles: Map<String, VirtualFile>
): FileTemplate {
  val json = gson.fromJson(String(file.inputStream.readBytes()), FileTemplateJson::class.java)
  return json.toFileTemplate(templateFiles)
}

private fun readSingleFileTemplate(
  file: VirtualFile,
  templateName: String,
  customProps: Set<FileTemplateCustomProp>
): FileTemplateSingle {
  return FileTemplateSingle(
    name = templateName,
    text = String(file.inputStream.readBytes()),
    customProps = customProps
  )
}

private fun FileTemplateJson.toFileTemplate(
  templateFiles: Map<String, VirtualFile>
): FileTemplate {
  return if (textFrom != null) {
    readFileTemplate(
      templateName = textFrom,
      templateFiles = templateFiles,
      customProps = customProps.toFileTemplateCustomProps()
    )
  } else {
    FileTemplateSingle(
      name = name ?: "",
      text = text ?: "",
      customProps = customProps.toFileTemplateCustomProps()
    )
  }
}

private fun List<FileTemplateCustomPropJson>?.toFileTemplateCustomProps(): Set<FileTemplateCustomProp> {
  return this?.map { FileTemplateCustomProp(it.name, it.text) }?.toSet() ?: emptySet()
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }
