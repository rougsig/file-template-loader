package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
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
  return readFileTemplate(templateName, fileRec)
}

fun readFileTemplate(
  templateName: String,
  templateFiles: List<VirtualFile>,
  customProps: List<FileTemplateCustomProp> = emptyList(),
  directory: String = ""
): FileTemplate {
  val templateFile = templateFiles.find { it.name == templateName }
    ?: throw IllegalStateException("template not found: $templateName")

  val templateNameWithoutExtension = FILE_TEMPLATE_EXTENSION_MATCHER.replace(templateName) { "" }

  return when {
    templateName.endsWith("$FILE_NAME_DELIMITER$FILE_TEMPLATE_GROUP_EXTENSION") ->
      readGroupFileTemplate(templateFile, templateNameWithoutExtension, templateFiles, customProps)
    else -> readSingleFileTemplate(templateFile, templateNameWithoutExtension, customProps, directory)
  }
}

private fun readGroupFileTemplate(
  file: VirtualFile,
  templateName: String,
  templateFiles: List<VirtualFile>,
  customProps: List<FileTemplateCustomProp>
): FileTemplateGroup {
  val json = gson.fromJson(String(file.inputStream.readBytes()), FileTemplateGroupJson::class.java)
  return FileTemplateGroup(
    name = json.name ?: templateName,
    templates = json.templates.map { it.toFileTemplate(templateFiles) },
    customProps = customProps.plus(json.customProps.toFileTemplateCustomProps())
  )
}

private fun readSingleFileTemplate(
  file: VirtualFile,
  templateName: String,
  customProps: List<FileTemplateCustomProp>,
  directory: String
): FileTemplateSingle {
  return FileTemplateSingle(
    name = templateName,
    text = String(file.inputStream.readBytes()),
    customProps = customProps,
    directory = directory
  )
}

private fun FileTemplateJson.toFileTemplate(
  templates: List<VirtualFile>
): FileTemplate {
  val fileNameProp = FileTemplateCustomProp(
    name = PROP_FILE_NAME,
    text = fileName
  )
  val customProps = listOf(fileNameProp).plus(customProps.toFileTemplateCustomProps())

  return if (textFrom != null) {
    readFileTemplate(textFrom, templates, customProps, directory ?: "")
  } else {
    FileTemplateSingle(
      name = "",
      text = text ?: "",
      customProps = customProps,
      directory = directory ?: ""
    )
  }
}

private fun List<FileTemplateCustomPropJson>?.toFileTemplateCustomProps(): List<FileTemplateCustomProp> {
  return this?.map { FileTemplateCustomProp(it.name, it.text) } ?: emptyList()
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }
