package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.entity.filterNotGenerated
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.google.gson.Gson
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class FileTemplatePropsTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testGetAllProps() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val templates = project.readFileTemplates()
    val viewFileTemplate = templates.find { it.name == "View" }!!

    val requiredProps = viewFileTemplate.getAllProps()

    assertSameElements(
      requiredProps,
      setOf(PROPS_PACKAGE_NAME, PROPS_FILE_NAME, "LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE")
    )
  }

  fun testGetRequiredProps() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val templates = project.readFileTemplates()
    val viewFileTemplate = templates.find { it.name == "View" }!!

    val props = Props().apply {
      setProperty(PROPS_PACKAGE_NAME, PROPS_PACKAGE_NAME)
    }
    val requiredProps = viewFileTemplate.getRequiredProps(props)

    assertSameElements(
      requiredProps,
      setOf("LAYOUT_SIMPLE_NAME", "LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE", PROPS_FILE_NAME)
    )
  }

  fun testGetGroupRequiredProps() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val templates = project.readFileTemplates()
    val groups = project.readFileTemplateGroups(templates, Gson())
    val viewFileTemplate = groups.find { it.name == "View" }!!

    val props = Props().apply {
      setProperty(PROPS_PACKAGE_NAME, PROPS_PACKAGE_NAME)
    }
    val requiredProps = viewFileTemplate.getRequiredProps(props)

    assertSameElements(
      requiredProps,
      setOf(
        "VIEW_NAME",
        "LAYOUT_SIMPLE_NAME",
        "LAYOUT_SIMPLE_NAME_UPPER_CAMEL_CASE",
        "LAYOUT_SIMPLE_NAME_LOWER_SNAKE_CASE",
        "VIEW_NAME_LOWER_SNAKE_CASE"
      )
    )
  }

  fun testGetGroupRequiredPropsFilterNotGenerated() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val templates = project.readFileTemplates()
    val groups = project.readFileTemplateGroups(templates, Gson())
    val viewFileTemplate = groups.find { it.name == "View" }!!

    val props = Props().apply {
      setProperty(PROPS_PACKAGE_NAME, PROPS_PACKAGE_NAME)
    }
    val requiredProps = viewFileTemplate.getRequiredProps(props).filterNotGenerated()

    assertSameElements(
      requiredProps,
      setOf(
        "VIEW_NAME"
      )
    )
  }

  fun testGenerateProps() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val templates = project.readFileTemplates()
    val viewFileTemplate = templates.find { it.name == "View" }!!

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
