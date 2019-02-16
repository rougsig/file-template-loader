package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_CLASS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_SIMPLE_NAME
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parents
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import junit.framework.TestCase
import java.io.File
import java.util.*

fun calculateTestDataPath(): String {
  val userDir = System.getProperty("user.dir")
  val dir = File(userDir ?: ".")
  return FileUtil.toCanonicalPath(dir.absolutePath) + "/testData"
}

fun assertFileTemplate(
  filePath: String,
  templateName: String,
  directory: String,
  props: Properties,
  actualFile: PsiFile,
  actualFileName: String,
  actualPackageName: String
) {
  LightPlatformCodeInsightFixtureTestCase.assertSameLinesWithFile(
    "${calculateTestDataPath()}/file-template-result/$filePath",
    actualFile.text
  )
  LightPlatformCodeInsightFixtureTestCase.assertEquals(
    actualFileName,
    props.getProperty(PROPS_SIMPLE_NAME(templateName))
  )
  LightPlatformCodeInsightFixtureTestCase.assertEquals(
    actualPackageName,
    props.getProperty(PROPS_CLASS_NAME(templateName))
  )
  TestCase.assertEquals(
    "\\src$directory",
    // FIXME PLEASE!!!
    actualFile.containingDirectory.parents().first().toString().replace("PsiDirectory:", "")
  )
}
