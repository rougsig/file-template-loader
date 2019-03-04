package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class FileTemplateSingleAdapter : JsonSerializer<FileTemplateSingle> {
  override fun serialize(template: FileTemplateSingle, type: Type, context: JsonSerializationContext): JsonElement {
    return JsonObject().apply {
      add("name", context.serialize(template.name))
      add("simpleName", context.serialize(template.simpleName))
      add("customProps", context.serialize(template.customProps))
      add("extractedProps", context.serialize(template.extractedProps))
      add("requiredProps", context.serialize(template.requiredProps))
      add("scope", context.serialize(template.scope))
    }
  }
}
