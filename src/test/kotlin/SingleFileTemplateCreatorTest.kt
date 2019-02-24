package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.creator.createSingleFileTemplate
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.fillPropsBySingleFileTemplate
import com.github.rougsig.filetemplateloader.generator.filterProps
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readSingleFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class SingleFileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("singleFileTemplateCreator", "")
  }

  private val defaultProps: Props
    get() = Props().apply {
      setProperty("DUCK_VOICE", "Quack Quack")
      setProperty("CAT_VOICE", "Meow Meow")
    }

  private fun doTest(testFileName: String, props: Props) {
    val testFile = myFixture.project
      .guessProjectDir()!!
      .findFileByRelativePath(testFileName)!!

    val template = readSingleFileTemplate(testFile)

    fillPropsBySingleFileTemplate(template, props)
    val generatedProps = template.requiredProps
      .filterProps(props)
      .generateProps(props)

    val createdFileTemplateFile = createSingleFileTemplate(project, generatedProps, template)

    val expectedFileName = testFileName.replace(".ft", "")
    val expectedMergedTemplate = myFixture.project
      .guessProjectDir()!!
      .findFileByRelativePath("$expectedFileName.txt")!!

    assertSameLines(
      String(expectedMergedTemplate.inputStream.readBytes()),
      createdFileTemplateFile.text
    )

    assertEquals(
      expectedFileName,
      createdFileTemplateFile.name
    )
  }

  fun testNoProps() = doTest("EmptyFileTemplate.kt.ft", Props())

  fun testGitignore() = doTest(".gitignore.ft", Props())

  fun testSimpleFileTemplate() = doTest("SimpleFileTemplate.kt.ft", defaultProps)
}
