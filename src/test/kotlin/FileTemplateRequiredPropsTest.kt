package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_GENERATORS
import com.github.rougsig.filetemplateloader.extension.getAllProps
import com.github.rougsig.filetemplateloader.extension.getGeneratedProps
import com.github.rougsig.filetemplateloader.extension.getGeneratedPropsBase
import com.github.rougsig.filetemplateloader.extension.getProps
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateRequiredPropsTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testGetAllProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val requiredProps = viewFileTemplate.text.getAllProps()

    assertSameElements(
      requiredProps,
      setOf("PACKAGE_NAME", "NAME", "LAYOUT_SIMPLE_NAME_LOWER_SHAKE_CASE")
    )
  }

  fun testGetGeneratedProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val requiredProps = viewFileTemplate.text.getGeneratedProps(PROPS_GENERATORS)

    assertSameElements(
      requiredProps,
      setOf("LAYOUT_SIMPLE_NAME_LOWER_SHAKE_CASE")
    )
  }

  fun testGetGeneratedPropsBase() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val requiredProps = viewFileTemplate.text.getGeneratedPropsBase(PROPS_GENERATORS)

    assertSameElements(
      requiredProps,
      setOf("LAYOUT_SIMPLE_NAME")
    )
  }

  fun testGetProps() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val viewFileTemplate = templates.find { it.fileName == "View" }!!

    val requiredProps = viewFileTemplate.text.getProps(PROPS_GENERATORS)

    assertSameElements(
      requiredProps,
      setOf("PACKAGE_NAME", "NAME", "LAYOUT_SIMPLE_NAME")
    )
  }
}
