package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_GENERATORS
import com.github.rougsig.filetemplateloader.entity.PropGenerator
import com.github.rougsig.filetemplateloader.extension.generateProps
import com.github.rougsig.filetemplateloader.extension.getGeneratedPropsBase
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class PropGeneratorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testGeneratorTransformation() {
    val generator = PropGenerator(
      "LOWER_CASE",
      String::toLowerCase
    )

    assertEquals(
      "hello_file_template",
      generator.generate("HELLO_FILE_TEMPLATE")
    )
  }

  fun testFileTemplateAddGenerated() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val props = Properties().apply {
      setProperty("LAYOUT_SIMPLE_NAME", "FileTemplate")
    }
    PROPS_GENERATORS.generateProps(props, viewFileTemplate.getGeneratedPropsBase(PROPS_GENERATORS))

    assertEquals(
      Properties().apply {
        setProperty("LAYOUT_SIMPLE_NAME", "FileTemplate")
        setProperty("LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE", "file_template")
        setProperty("LAYOUT_SIMPLE_NAME_UPPER_SNAKE_CASE", "FILE_TEMPLATE")
        setProperty("LAYOUT_SIMPLE_NAME_LOWER_CAMEL_CASE", "fileTemplate")
        setProperty("LAYOUT_SIMPLE_NAME_UPPER_CAMEL_CASE", "FileTemplate")
      },
      props
    )
  }
}
