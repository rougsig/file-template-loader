package com.github.rougsig.filetemplateloader

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
    }

  private fun doTest(testFileName: String, props: Props) {
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

  fun testNoProps() = doTest("NoProps.ft", Props())

  fun testNoGeneratedProps() = doTest("NoGeneratedProps.ft", defaultProps)

  fun testGeneratedProps() = doTest("GeneratedProps.ft", defaultProps)
}
