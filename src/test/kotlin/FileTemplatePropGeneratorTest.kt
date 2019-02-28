package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
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

    if (template is FileTemplateSingle) {
      props.setProperty(PROP_FILE_NAME, testFileName.replace(".ft", ""))
    }

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

  fun testGitignore() = doTest(".gitignore.ft")

  fun testEmptyFileTemplate() = doTest("EmptyFileTemplate.kt.ft")

  fun testSimpleFileTemplate() = doTest("SimpleFileTemplate.kt.ft")

  fun testRepositoryGroup() = doTest("Repository.group.json")

  fun testScreenGroup() = doTest("Screen.group.json")
}
