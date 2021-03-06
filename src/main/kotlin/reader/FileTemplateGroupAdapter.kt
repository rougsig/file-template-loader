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
      add("simpleName", context.serialize(template.simpleName))
      add("templates", context.serialize(template.templates))
      add("injectors", context.serialize(template.injectors))
      add("customProps", context.serialize(template.customProps))
      add("extractedProps", context.serialize(template.extractedProps))
      add("requiredProps", context.serialize(template.requiredProps))
      add("scope", context.serialize(template.scope))
    }
  }
}
