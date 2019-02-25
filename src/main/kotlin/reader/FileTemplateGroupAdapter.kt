package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class FileTemplateGroupAdapter : JsonSerializer<FileTemplateGroup> {
  override fun serialize(template: FileTemplateGroup, type: Type, context: JsonSerializationContext): JsonElement {
    return JsonObject().apply {
      add("name", context.serialize(template.name))
      add("templates", context.serialize(template.templates))
      add("directory", context.serialize(template.directory))
      add("customProps", context.serialize(template.customProps))
      add("requiredProps", context.serialize(template.requiredProps))
    }
  }
}
