package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PROPS_MODULE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_MODULE_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME_TEMPLATE
import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.entity.generateProps
import com.github.rougsig.filetemplateloader.entity.getTemplateProps
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.util.projectStructure.module

fun Props.generatePackageName(dir: PsiDirectory? = null) {
  val props = this
  val packageNameTemplate = props.getProperty(PROPS_PACKAGE_NAME_TEMPLATE)
  val templateProps = getTemplateProps(packageNameTemplate)
  generateProps(templateProps, props)

  val packageName = StringBuilder()
  packageName.append(mergeTemplate(packageNameTemplate, props))
  props.setProperty(PROPS_MODULE_PACKAGE_NAME, packageName.toString())

  val subPackage = dir?.getPackage()?.qualifiedName
  if (!subPackage.isNullOrBlank()) {
    packageName.append(".")
    packageName.append(subPackage)
  }
  props.setProperty(PROPS_PACKAGE_NAME, packageName.toString())
}

fun PsiDirectory.generateModuleSimpleName(props: Props) {
  val moduleName = ModuleUtil.getModuleDirPath(module!!)
    .replace("\\", "/")
    .split("/")
    .last()
  props.setProperty(PROPS_MODULE_NAME, moduleName)
}
