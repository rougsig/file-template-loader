package com.github.rougsig.filetemplateloader.reader

import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.scope.PropScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder

private fun createGson(): Gson {
  return GsonBuilder()
    .registerTypeAdapter(FileTemplateSingle::class.java, FileTemplateSingleAdapter())
    .registerTypeAdapter(FileTemplateGroup::class.java, FileTemplateGroupAdapter())
    .registerTypeAdapter(PropScope::class.java, PropScopeAdapter())
    .create()
}

val gson by lazy { createGson() }
