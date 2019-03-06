package com.github.rougsig.filetemplateloader.entity

import com.github.rougsig.filetemplateloader.scope.PropScope

abstract class ScopedFileTemplate : NamedFileTemplate() {
  abstract val customProps: Set<FileTemplateCustomProp>

  abstract val scope: PropScope
}
