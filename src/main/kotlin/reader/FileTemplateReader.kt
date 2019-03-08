package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.*
import com.google.gson.JsonObject
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun Project.readFileTemplate(templateFileName: String): ScopedFileTemplate {
  return guessProjectDir()!!
    .findChild(FILE_TEMPLATE_FOLDER_NAME)!!
    .readFileTemplate(templateFileName)
}

fun VirtualFile.readFileTemplate(templateFileName: String): ScopedFileTemplate {
  return readFileTemplate(
    templateFileName,
    fileRec.map { it.name to it }.toMap()
  )
}

private fun readFileTemplate(
  templateFileName: String,
  templateFiles: Map<String, VirtualFile>,
  parentCustomProps: Set<FileTemplateCustomProp>? = null
): ScopedFileTemplate {
  val templateFile = templateFiles[templateFileName]
    ?: throw IllegalStateException("template not found: $templateFileName")

  val templateFileNameWithoutExtension = FILE_TEMPLATE_EXTENSION_MATCHER.replace(templateFileName) { "" }

  return when {
    templateFileName.endsWith(FILE_TEMPLATE_GROUP_EXTENSION) ->
      readGroupFileTemplate(templateFile, templateFileNameWithoutExtension, templateFiles, parentCustomProps)

    templateFileName.endsWith(FILE_TEMPLATE_TEMPLATE_EXTENSION) ->
      readTemplateFileTemplate(templateFile, templateFiles)

    templateFileName.endsWith(FILE_TEMPLATE_FT_EXTENSION) ->
      readSingleFileTemplate(templateFile, templateFileNameWithoutExtension, parentCustomProps)

    else ->
      throw IllegalStateException("unknown template type: $templateFileName")
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
    templates = json.templates.map { it.toFileTemplate(templateFiles) },
    injectors = (json.injectors ?: emptyList()).map { it.toFileTemplateInjector() },
    initialCustomProps = (parentCustomProps ?: emptySet()).plus(json.variables.toFileTemplateCustomProps())
  )
}

private fun readTemplateFileTemplate(
  file: VirtualFile,
  templateFiles: Map<String, VirtualFile>
): ScopedFileTemplate {
  return gson
    .fromJson(String(file.inputStream.readBytes()), FileTemplateJson::class.java)
    .toFileTemplate(templateFiles)
}

private fun readSingleFileTemplate(
  file: VirtualFile,
  templateName: String,
  parentCustomProps: Set<FileTemplateCustomProp>?
): FileTemplateSingle {
  return FileTemplateSingle(
    name = templateName,
    text = String(file.inputStream.readBytes()),
    initialCustomProps = parentCustomProps ?: emptySet()
  )
}

private fun FileTemplateJson.toFileTemplate(
  templateFiles: Map<String, VirtualFile>
): ScopedFileTemplate {
  val customProps = variables.toFileTemplateCustomProps()

  return if (contentFrom != null) {
    readFileTemplate(
      templateFileName = contentFrom,
      templateFiles = templateFiles,
      parentCustomProps = customProps
    )
  } else {
    FileTemplateSingle(
      name = name ?: "",
      text = content ?: "",
      initialCustomProps = customProps
    )
  }
}

private fun FileTemplateInjectorJson.toFileTemplateInjector(): FileTemplateInjector {
  return FileTemplateInjector(
    text = text,
    selector = selector,
    className = className,
    pathName = pathName
  )
}

private fun JsonObject?.toFileTemplateCustomProps(): Set<FileTemplateCustomProp> {
  return this?.entrySet()?.map { (k, v) ->
    FileTemplateCustomProp(k, v.asString)
  }?.toSet() ?: emptySet()
}

private val VirtualFile.fileRec: List<VirtualFile>
  get() {
    return if (isDirectory) children.toList().flatMap { it.fileRec }
    else listOf(this)
  }
