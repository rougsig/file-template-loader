package com.github.rougsig.filetemplateloader.di

import toothpick.config.Module
import toothpick.config.ModuleBindings
import com.github.rougsig.filetemplateloader.FileTemplateRepository
import com.github.rougsig.filetemplateloader.FileTemplateRepositoryImpl

object FileTemplateRepositoryBindings : ModuleBindings {
  override fun bindInto(module: Module) {
    module
      .bind(FileTemplateRepository::class.java)
      .to(FileTemplateRepositoryImpl::class.java)
      .singletonInScope()
  }
}
