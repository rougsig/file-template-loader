package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplatePropGeneratorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("", "")
  }

  private fun doTest(testFileName: String, props: Props = DEFAULT_PROPS) {
    val template = project.guessProjectDir()!!.readFileTemplate(testFileName)

    val generatedProps = template
      .generateProps(props)
      .map { (key, value) -> "$key=$value" }
      .sorted()
      .joinToString("\n") { it }

    assertSameLinesWithFile(
      "$testDataPath/singleFileTemplateGenerateProps/$testFileName.txt",
      generatedProps
    )
  }

  fun testNoProps() = doTest(".gitignore.ft")

  fun testNoGeneratedProps() = doTest("EmptyFileTemplate.kt.ft")

  fun testGeneratedProps() = doTest("SimpleFileTemplate.kt.ft")
}
