package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("", "")
  }

  private fun doTest(testFileName: String, props: Props = DEFAULT_PROPS) {
    val fileName = testFileName.replace(".ft", "")
    val template = project.guessProjectDir()!!.readFileTemplate(testFileName)
    val dir = psiManager.findDirectory(project.guessProjectDir()!!)!!

    props.setProperty(PROP_FILE_NAME, fileName)
    val generatedProps = template.generateProps(props)

    val createdFileTemplateFile = project.writeAction {
      template
        .create(dir, generatedProps)
        .first()
    }

    val expectedMergedTemplate = myFixture.project
      .guessProjectDir()!!
      .findChild("singleFileTemplateCreator")!!
      .findChild("$testFileName.txt")!!

    assertSameLines(
      String(expectedMergedTemplate.inputStream.readBytes()),
      createdFileTemplateFile.text
    )

    assertEquals(
      fileName,
      createdFileTemplateFile.name
    )
  }

  fun testEmptyFileTemplate() = doTest("EmptyFileTemplate.kt.ft")

  fun testGitignore() = doTest(".gitignore.ft")

  fun testSimpleFileTemplate() = doTest("SimpleFileTemplate.kt.ft")
}
