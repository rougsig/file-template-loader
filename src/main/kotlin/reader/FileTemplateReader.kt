package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.*
import com.google.gson.JsonObject
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

private typealias TemplateFiles = Map<String, VirtualFile>
private typealias TemplateMixins = Map<String, FileTemplateMixin>

fun TemplateFiles.requireTemplate(fileName: String): VirtualFile {
  return this[fileName] ?: throw IllegalStateException("template not found: $fileName")
}

fun TemplateMixins.requireMixin(mixinName: String): FileTemplateMixin {
  return this[mixinName] ?: throw IllegalStateException("mixin not found: $mixinName")
}

fun TemplateMixins.findMatches(templateName: String): Set<FileTemplateMixin> {
  return this
    .filter { (_, v) -> v.pattern?.containsMatchIn(templateName) ?: false }
    .values
    .toSet()
}

fun Project.readFileTemplate(templateFileName: String): ScopedFileTemplate {
  return guessProjectDir()!!
    .findChild(FILE_TEMPLATE_FOLDER_NAME)!!
    .readFileTemplate(templateFileName)
}

fun VirtualFile.readFileTemplate(templateFileName: String): ScopedFileTemplate {
  val templateFiles = fileRec.map { it.name to it }.toMap()
  val templateMixins = templateFiles
    .filter { (k, _) -> k.endsWith(FILE_TEMPLATE_MIXIN_EXTENSION) }
    .map { (k, v) -> k to v.readFileTemplateMixin() }
    .toMap()

  return readFileTemplate(
    templateFileName,
    emptySet(),
    templateFiles,
    templateMixins
  )
}

private fun readFileTemplate(
  templateFileName: String,
  parentCustomProps: Set<FileTemplateCustomProp>,
  templateFiles: TemplateFiles,
  templateMixins: TemplateMixins
): ScopedFileTemplate {
  val templateFile = templateFiles.requireTemplate(templateFileName)
  val templateName = TEMPLATE_EXTENSION_MATCHER.replace(templateFileName) { "" }

  return when {
    templateFileName.endsWith(JSON_EXTENSION) -> {
      val json = gson.fromJson(String(templateFile.inputStream.readBytes()), JsonObject::class.java)

      when {
        json.has("content") || json.has("contentFrom") ->
          templateFile.readTemplateFileTemplate(templateFiles, templateMixins)

        json.has("templates") ->
          templateFile.readGroupFileTemplate(templateName, parentCustomProps, templateFiles, templateMixins)

        else ->
          throw IllegalStateException("unknown template json type: $templateFileName")
      }
    }

    templateFileName.endsWith(FT_EXTENSION) ->
      templateFile.readSingleFileTemplate(templateName, parentCustomProps, templateMixins)

    else ->
      throw IllegalStateException("unknown template type: $templateFileName")
  }
}

private fun VirtualFile.readGroupFileTemplate(
  templateName: String,
  parentCustomProps: Set<FileTemplateCustomProp>,
  templateFiles: TemplateFiles,
  templateMixins: TemplateMixins
): FileTemplateGroup {
  val json = gson.fromJson(String(inputStream.readBytes()), FileTemplateGroupJson::class.java)
  return FileTemplateGroup(
    fullName = json.name ?: templateName,
    templates = json.templates.toFileTemplates(templateFiles, templateMixins),
    injectors = json.injectors.toFileTemplateInjectors(),
    initialCustomProps = parentCustomProps
      .plus(json.variables.toFileTemplateCustomProps())
      .plus(json.mixins.readFileTemplateMixins(templateMixins).flatMap(FileTemplateMixin::customProps))
      .plus(templateMixins.findMatches(name).flatMap(FileTemplateMixin::customProps))
  )
}

private fun VirtualFile.readTemplateFileTemplate(
  templateFiles: TemplateFiles,
  templateMixins: TemplateMixins
): ScopedFileTemplate {
  return gson
    .fromJson(String(inputStream.readBytes()), FileTemplateJson::class.java)
    .toFileTemplate(this.name, templateFiles, templateMixins)
}

private fun VirtualFile.readSingleFileTemplate(
  templateName: String,
  parentCustomProps: Set<FileTemplateCustomProp>,
  templateMixins: TemplateMixins
): FileTemplateSingle {
  return FileTemplateSingle(
    fullName = templateName,
    text = String(inputStream.readBytes()),
    initialCustomProps = parentCustomProps
      .plus(templateMixins.findMatches(name).flatMap(FileTemplateMixin::customProps))
  )
}

private fun List<FileTemplateJson>.toFileTemplates(
  templateFiles: TemplateFiles,
  templateMixins: TemplateMixins
): List<ScopedFileTemplate> {
  return map { it.toFileTemplate("", templateFiles, templateMixins) }
}

private fun FileTemplateJson.toFileTemplate(
  fileName: String,
  templateFiles: TemplateFiles,
  templateMixins: TemplateMixins
): ScopedFileTemplate {
  val customProps = variables.toFileTemplateCustomProps()

  return if (contentFrom != null) {
    readFileTemplate(
      templateFileName = contentFrom,
      parentCustomProps = customProps,
      templateFiles = templateFiles,
      templateMixins = templateMixins
    )
  } else {
    FileTemplateSingle(
      fullName = name ?: "",
      text = content ?: "",
      initialCustomProps = customProps
        .plus(templateMixins.findMatches(fileName).flatMap(FileTemplateMixin::customProps))
    )
  }
}

private fun List<FileTemplateInjectorJson>?.toFileTemplateInjectors(): List<FileTemplateInjector> {
  return this?.map { it.toFileTemplateInjector() } ?: emptyList()
}

private fun FileTemplateInjectorJson.toFileTemplateInjector(): FileTemplateInjector {
  return FileTemplateInjector(
    text = text,
    selector = selector,
    className = className,
    pathName = pathName
  )
}

private fun List<String>?.readFileTemplateMixins(
  templateMixins: TemplateMixins
): List<FileTemplateMixin> {
  return this?.map { templateMixins.requireMixin(it) } ?: emptyList()
}

private fun VirtualFile.readFileTemplateMixin(): FileTemplateMixin {
  val json = gson.fromJson(String(inputStream.readBytes()), FileTemplateMixinJson::class.java)
  return FileTemplateMixin(
    name = TEMPLATE_EXTENSION_MATCHER.replace(name) { "" },
    pattern = json.pattern?.toRegex(),
    customProps = json.variables.toFileTemplateCustomProps()
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
