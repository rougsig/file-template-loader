package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.*
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.readFileTemplate(templateFileName: String): FileTemplate {
  return readFileTemplate(
    templateFileName,
    fileRec.map { it.name to it }.toMap()
  )
}

private fun readFileTemplate(
  templateFileName: String,
  templateFiles: Map<String, VirtualFile>,
  directory: String? = null,
  parentCustomProps: Set<FileTemplateCustomProp>? = null
): FileTemplate {
  val templateFile = templateFiles[templateFileName]
    ?: throw IllegalStateException("template not found: $templateFileName")

  val templateFileNameWithoutExtension = FILE_TEMPLATE_EXTENSION_MATCHER.replace(templateFileName) { "" }

  return when {
    templateFileName.endsWith(FILE_TEMPLATE_GROUP_EXTENSION) ->
      readGroupFileTemplate(templateFile, templateFileNameWithoutExtension, templateFiles, parentCustomProps)

    templateFileName.endsWith(FILE_TEMPLATE_TEMPLATE_EXTENSION) ->
      readTemplateFileTemplate(templateFile, templateFiles)

    else ->
      readSingleFileTemplate(templateFile, templateFileNameWithoutExtension, directory, parentCustomProps)
  }
}

private fun readGroupFileTemplate(
  file: VirtualFile,
  templateName: String,
  templateFiles: Map<String, VirtualFile>,
  parentCustomProps: Set<FileTemplateCustomProp>?
): FileTemplateGroup {
  val json = gson.fromJson(String(file.inputStream.readBytes()), FileTemplateGroupJson::class.java)
  return FileTemplateGroup(
    name = json.name ?: templateName,
    templates = json.templates.map {
      it.toFileTemplate(templateFiles)
    },
    initialCustomProps = (parentCustomProps ?: emptySet()).plus(json.customProps.toFileTemplateCustomProps())
  )
}

private fun readTemplateFileTemplate(
  file: VirtualFile,
  templateFiles: Map<String, VirtualFile>
): FileTemplate {
  return gson
    .fromJson(String(file.inputStream.readBytes()), FileTemplateJson::class.java)
    .toFileTemplate(templateFiles)
}

private fun readSingleFileTemplate(
  file: VirtualFile,
  templateName: String,
  directory: String?,
  parentCustomProps: Set<FileTemplateCustomProp>?
): FileTemplateSingle {
  return FileTemplateSingle(
    name = templateName,
    text = String(file.inputStream.readBytes()),
    directory = directory ?: "",
    initialCustomProps = parentCustomProps ?: emptySet()
  )
}

private fun FileTemplateJson.toFileTemplate(
  templateFiles: Map<String, VirtualFile>
): FileTemplate {
  val customProps = customProps.toFileTemplateCustomProps()

  return if (textFrom != null) {
    readFileTemplate(
      templateFileName = textFrom,
      templateFiles = templateFiles,
      directory = directory,
      parentCustomProps = customProps
    )
  } else {
    FileTemplateSingle(
      name = name ?: "",
      text = text ?: "",
      directory = directory ?: "",
      initialCustomProps = customProps
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
