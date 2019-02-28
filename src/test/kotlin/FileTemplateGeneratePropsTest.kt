package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateGeneratePropsTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("singleFileTemplateGenerateProps", "")
  }

  private val defaultProps: Props
    get() = Props().apply {
      setProperty("ONE_PROP", "OneProp")
      setProperty("TWO_PROP", "TwoProp")
      setProperty("THREE_PROP", "ThreeProp")
      setProperty(PROP_PACKAGE_NAME, PROP_PACKAGE_NAME_TEST_VALUE)
    }

  private fun doTest(testFileName: String, props: Props = defaultProps) {
    val template = project.guessProjectDir()!!.readFileTemplate(testFileName)

    val generatedProps = template
      .generateProps(props)
      .map { (key, value) -> "$key=$value" }
      .sorted()
      .joinToString("\n") { it }

    val expectedProps = project
      .guessProjectDir()!!
      .findFileByRelativePath("$testFileName.txt")!!

    assertSameLines(
      String(expectedProps.inputStream.readBytes()),
      generatedProps
    )
  }

  fun testNoProps() = doTest("NoProps.ft")

  fun testNoGeneratedProps() = doTest("NoGeneratedProps.ft")

  fun testGeneratedProps() = doTest("GeneratedProps.ft")
}
