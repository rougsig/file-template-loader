package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.filterProps
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

    val extractedProps = template.requiredProps
      .filterProps(defaultProps)
      .sorted()
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

  fun testSimpleProps() = doTest("SimpleProps.ft")

  fun testGeneratedProps() = doTest("GeneratedProps.ft")

  fun testClassNameProps() = doTest("ClassNameProps.ft")

  fun testDefaultProps() = doTest("DefaultProps.ft", Props().apply { setProperty("ONE_PROP", "ONE_PROP") })
}
