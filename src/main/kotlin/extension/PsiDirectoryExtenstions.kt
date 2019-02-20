package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PROPS_MODULE_SIMPLE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME_TEMPLATE
import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.entity.generateProps
import com.github.rougsig.filetemplateloader.entity.getTemplateProps
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.util.projectStructure.module

fun Props.generatePackageName() {
  val props = this
  val packageNameTemplate = props.getProperty(PROPS_PACKAGE_NAME_TEMPLATE)
  val templateProps = getTemplateProps(packageNameTemplate)
  generateProps(templateProps, props)

  val moduleName = mergeTemplate(packageNameTemplate, props)
  props.setProperty(PROPS_PACKAGE_NAME, moduleName)
}

fun PsiDirectory.generateModuleSimpleName(props: Props) {
  val moduleName = ModuleUtil.getModuleDirPath(module!!)
    .replace("\\", "/")
    .split("/")
    .last()
  props.setProperty(PROPS_MODULE_SIMPLE_NAME, moduleName)
}
