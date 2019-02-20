package com.github.rougsig.filetemplateloader.epic.repository.di

import toothpick.config.Module
import toothpick.config.ModuleBindings
import com.github.rougsig.filetemplateloader.epic.repository.EpicRepository
import com.github.rougsig.filetemplateloader.epic.repository.EpicRepositoryImpl

object EpicRepositoryBindings : ModuleBindings {
  override fun bindInto(module: Module) {
    module
      .bind(EpicRepository::class.java)
      .to(EpicRepositoryImpl::class.java)
      .singletonInScope()
  }
}
