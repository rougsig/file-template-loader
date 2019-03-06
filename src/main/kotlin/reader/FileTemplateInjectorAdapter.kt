package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateInjector
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class FileTemplateInjectorAdapter : JsonSerializer<FileTemplateInjector> {
  override fun serialize(template: FileTemplateInjector, type: Type, context: JsonSerializationContext): JsonElement {
    return JsonObject().apply {
      add("selector", context.serialize(template.selector))
      add("className", context.serialize(template.className))
      add("pathName", context.serialize(template.pathName))
      add("requiredProps", context.serialize(template.requiredProps))
    }
  }
}
