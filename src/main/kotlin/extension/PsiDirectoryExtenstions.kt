package com.github.rougsig.filetemplateloader.extension

import com.github.rougsig.filetemplateloader.constant.PROP_MODULE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME_TEMPLATE
import com.github.rougsig.filetemplateloader.constant.PROP_ROOT_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.mergeTemplate
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.extractProps
import com.github.rougsig.filetemplateloader.generator.generatePropModificators
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.util.projectStructure.module

fun Props.generatePackageName(dir: PsiDirectory? = null) {
  val props = this
  val packageNameTemplate = props.getProperty(PROP_PACKAGE_NAME_TEMPLATE)
  extractProps(packageNameTemplate).generatePropModificators(props)

  val packageName = StringBuilder()
  packageName.append(mergeTemplate(packageNameTemplate, props))
  props.setProperty(PROP_ROOT_PACKAGE_NAME, packageName.toString())

  val subPackage = dir?.getPackage()?.qualifiedName
  if (!subPackage.isNullOrBlank()) {
    packageName.append(".")
    packageName.append(subPackage)
  }
  props.setProperty(PROP_PACKAGE_NAME, packageName.toString())
}

fun PsiDirectory.generateModuleSimpleName(props: Props) {
  val moduleName = ModuleUtil.getModuleDirPath(module!!)
    .replace("\\", "/")
    .split("/")
    .last()
  props.setProperty(PROP_MODULE_NAME, moduleName)
}
