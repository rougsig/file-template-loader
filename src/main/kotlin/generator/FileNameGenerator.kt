package com.github.rougsig.filetemplateloader.generator

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.entity.mergeTemplate

fun generateFileName(props: Props, template: FileTemplateSingle): String {
  val fileNameTemplate = template.fileName ?: props.getProperty(PROP_FILE_NAME)
  return mergeTemplate(fileNameTemplate, props)
}

fun generateFileNameWithExtension(props: Props, template: FileTemplateSingle): String {
  return "${generateFileName(props, template)}.${template.extension}"
}
