package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.scope.PropScope
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class PropScopeAdapter : JsonSerializer<PropScope> {
  override fun serialize(scope: PropScope, type: Type, context: JsonSerializationContext): JsonElement {
    return JsonObject().apply {
      add("name", context.serialize(scope.name))
      add("childScopes", context.serialize(scope.childScopes))
      add("scopedPropGenerators", context.serialize(scope.scopedPropGenerators))
    }
  }
}
