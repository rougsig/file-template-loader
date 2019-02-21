package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.filterProps
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.reader.readSingleFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class SingleFileTemplateExtractPropsTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("singleFileTemplateExtractProps", "")
  }

  private fun doTest(testFileName: String, defaultProps: Props = Props()) {
    val testFile = myFixture.project
      .guessProjectDir()!!
      .findFileByRelativePath(testFileName)!!

    val template = readSingleFileTemplate(testFile)

    val extractedProps = template.extractedProps
      .filterProps(defaultProps)
      .joinToString("\n") { it }

    val expectedProps = myFixture.project
      .guessProjectDir()!!
      .findFileByRelativePath("${template.name}.txt")!!

    assertSameLines(
      String(expectedProps.inputStream.readBytes()),
      extractedProps
    )
  }

  fun testNoProps() = doTest("NoProps.ft")

  fun testOneProp() = doTest("OneProp.ft")

  fun testTwoProp() = doTest("TwoProp.ft")

  fun testOneGeneratedProp() = doTest("OneGeneratedProp.ft")

  fun testClassNameProp() = doTest("ClassName.ft")
}
