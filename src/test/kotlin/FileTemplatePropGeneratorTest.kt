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
      "$testDataPath/fileTemplatePropGenerator/$testFileName.txt",
      generatedProps
    )
  }

  fun testGitignoreFt() = doTest(".gitignore.ft")

  fun testGitignoreTemplateJson() = doTest(".gitignore.template.json")

  fun testRepositoryTemplateJson() = doTest("Repository.template.json")

  fun testRepositoryGroupJson() = doTest("Repository.group.json")
}
