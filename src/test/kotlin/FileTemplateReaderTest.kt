package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.FILE_TEMPLATE_FOLDER_NAME
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  private val gson = createGson()

  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("fileTemplateReader", "")
  }

  private fun doTest(testFileName: String) {
    val template = project
      .guessProjectDir()!!
      .findChild(FILE_TEMPLATE_FOLDER_NAME)!!
      .readFileTemplate(testFileName)

    val json = gson.toJson(template)

    val expectedJson = project
      .guessProjectDir()!!
      .findChild("$testFileName.txt")!!

    assertSameLines(
      String(expectedJson.inputStream.readBytes()),
      json
    )
  }

  fun testEmptyFileTemplate() = doTest("EmptyFileTemplate.kt.ft")

  fun testSimpleFileTemplate() = doTest("SimpleFileTemplate.kt.ft")
}
