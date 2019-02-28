package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROP_PACKAGE_NAME
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
    myFixture.copyDirectoryToProject("singleFileTemplateCreator", "")
  }

  private val defaultProps: Props
    get() = Props().apply {
      setProperty("DUCK_VOICE", "Quack Quack")
      setProperty("CAT_VOICE", "Meow Meow")
      setProperty(PROP_PACKAGE_NAME, PROP_PACKAGE_NAME_TEST_VALUE)
    }

  private fun doTest(testFileName: String, props: Props = defaultProps) {
    val fileName = testFileName.replace(".ft", "")
    val template = project.guessProjectDir()!!.readFileTemplate(testFileName)
    val dir = psiManager.findDirectory(project.guessProjectDir()!!)!!

    props.setProperty(PROP_FILE_NAME, fileName)
    template.generateProps(props)

    val createdFileTemplateFile = project.writeAction {
      template
        .create(dir, props)
        .first()
    }

    val expectedMergedTemplate = myFixture.project
      .guessProjectDir()!!
      .findFileByRelativePath("$testFileName.txt")!!

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
