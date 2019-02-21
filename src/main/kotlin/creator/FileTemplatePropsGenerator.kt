package com.github.rougsig.filetemplateloader.creator

import com.github.rougsig.filetemplateloader.entity.Props

interface FileTemplatePropsGenerator {
  fun getAllProps(): Set<String>
  fun getRequiredProps(props: Props): Set<String>
  fun generateProps(props: Props): Props
}
