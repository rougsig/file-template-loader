package com.github.rougsig.ftl

import com.github.rougsig.ftl.io.Directory
import com.github.rougsig.ftl.io.File
import com.github.rougsig.ftl.io.toVirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import java.io.BufferedInputStream

internal const val FTL_LIB_NAME = "ftl"
internal val libNames = listOf("ftl-runtime.jar", "kotlin-script-runtime.jar", "kotlin-stdlib.jar")

internal fun Project.createFtlLibrary(directory: Directory): Library {
  directory.toVirtualFile().children.forEach { it.delete(null) }
  val modifiableLibTable = LibraryTablesRegistrar.getInstance().getLibraryTable(this).modifiableModel
  modifiableLibTable.libraries.find { it.name == FTL_LIB_NAME }?.let { modifiableLibTable.removeLibrary(it) }
  val lib = modifiableLibTable.createLibrary(FTL_LIB_NAME)
  val modifiableLib = lib.modifiableModel
  libNames.forEach { libName ->
    val jarFile = directory.createFile(libName)
    extractJarTo(libName, jarFile)
    modifiableLib.addJarDirectory(jarFile.toVirtualFile(), true, OrderRootType.CLASSES)
    modifiableLib.addJarDirectory(jarFile.toVirtualFile(), true, OrderRootType.SOURCES)
    modifiableLib.addJarDirectory(jarFile.toVirtualFile(), true, OrderRootType.DOCUMENTATION)
  }
  modifiableLib.commit()
  modifiableLibTable.commit()
  return lib
}

private fun extractJarTo(jarName: String, file: File) {
  val inputStream = FtlProjectModule::class.java.classLoader.getResourceAsStream(jarName)
    ?: error("jar not found with name $jarName")
  val bytes = BufferedInputStream(inputStream).readBytes()
  java.io.File(file.pathName).writeBytes(bytes)
}
