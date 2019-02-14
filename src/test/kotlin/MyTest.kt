package com.github.rougsig.filetemplateloader

import com.intellij.openapi.util.io.FileUtil
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.io.File

class MyTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String {
    val userDir = System.getProperty("user.dir")
    val dir = File(userDir ?: ".")
    return FileUtil.toCanonicalPath(dir.absolutePath) + "/testData"
  }

  fun testNothing() {
    val copied = myFixture.copyDirectoryToProject(".fileTemplates", "")
    val templates = myFixture.psiManager.findDirectory(copied)!!

    templates.children.forEach(::println)
  }
}
