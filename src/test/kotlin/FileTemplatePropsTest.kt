package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class FileTemplatePropsTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testGetAllProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val requiredProps = viewFileTemplate.getAllProps()

    assertSameElements(
      requiredProps,
      setOf(PROPS_PACKAGE_NAME, PROPS_NAME, "LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE")
    )
  }

  fun testGetRequiredProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val props = Props().apply {
      setProperty(PROPS_PACKAGE_NAME, PROPS_PACKAGE_NAME)
      setProperty(PROPS_NAME, PROPS_NAME)
    }
    val requiredProps = viewFileTemplate.getRequiredProps(props)

    assertSameElements(
      requiredProps,
      setOf("LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE")
    )
  }

  fun testGenerateProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val props = Props().apply {
      setProperty("LAYOUT_SIMPLE_NAME", "FileTemplate")
    }
    viewFileTemplate.generateProps(props)
    assertEquals(
      Properties().apply {
        setProperty("LAYOUT_SIMPLE_NAME", "FileTemplate")
        setProperty("LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE", "file_template")
      },
      props
    )
  }
}
