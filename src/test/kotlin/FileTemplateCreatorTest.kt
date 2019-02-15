package com.github.rougsig.filetemplateloader

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.util.*

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testCalculatePackageName() {
    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val packageName = dir.calculatePackageName()

    assertEquals("com.github.rougsig.filetemplateloader", packageName)
  }

  fun testCreateFileTemplate() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val config = readConfig(fileTemplateDirectory)

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val repositoryFileTemplate = templates.find { it.name == "Repository" }!!

    val props = Properties(config)
    props.setProperty(PROPS_FILE_NAME, "FileTemplateRepository")
    val template = repositoryFileTemplate.create(dir, props)

    assertSameLinesWithFile(
      "$testDataPath/file-template-creator/.fileTemplates/repository/Repository.kt",
      template.text
    )
    assertEquals(
      "FileTemplateRepository",
      props.getProperty(PROPS_SIMPLE_NAME("repository"))
    )
    assertEquals(
      "com.github.rougsig.filetemplateloader.FileTemplateRepository",
      props.getProperty(PROPS_CLASS_NAME("repository"))
    )
  }
}
