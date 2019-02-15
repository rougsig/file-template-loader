package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_CLASS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_SIMPLE_NAME
import com.github.rougsig.filetemplateloader.creator.create
import com.github.rougsig.filetemplateloader.extension.calculatePackageName
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.google.gson.Gson
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.util.*

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testCalculatePackageName() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val config = readConfig(fileTemplateDirectory)

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val packageName = dir.calculatePackageName(config)

    assertEquals("com.github.rougsig.light.idea.test.case", packageName)
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
    props.setProperty(PROPS_NAME, "FileTemplateRepository")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val template = project.writeAction("Create FileTemplate: ${repositoryFileTemplate.name}") {
      repositoryFileTemplate.create(dir, props) as PsiFile
    }
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

  fun testCreateFileTemplateGroup() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val templateGroups = readFileTemplateGroups(templates, fileTemplateDirectory, Gson())
    val config = readConfig(fileTemplateDirectory)

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val repositoryFileTemplateGroup = templateGroups.find { it.name == "Repository" }
  }
}
