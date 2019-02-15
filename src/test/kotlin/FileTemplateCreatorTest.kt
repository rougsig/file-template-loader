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
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import junit.framework.TestCase
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

    val repositoryFileTemplate = templates.find { it.fileName == "Repository" }!!

    val props = Properties(config)
    props.setProperty(PROPS_NAME, "FileTemplateRepository")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val template = project.writeAction {
      repositoryFileTemplate.create(dir, props)
    }
    assertSameLinesWithFile(
      "$testDataPath/file-template-result/repository/FileTemplateRepository.kt",
      template.text
    )
    assertEquals(
      "FileTemplateRepository",
      props.getProperty(PROPS_SIMPLE_NAME("Repository"))
    )
    assertEquals(
      "com.github.rougsig.filetemplateloader.FileTemplateRepository",
      props.getProperty(PROPS_CLASS_NAME("Repository"))
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

    val repositoryFileTemplateGroup = templateGroups.find { it.name == "Repository" }!!

    val props = Properties(config)
    props.setProperty("FLOW_NAME", "FileTemplate")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val group = project.writeAction {
      repositoryFileTemplateGroup.create(dir, props)
    }

    val repository = group.find { it.name == "FileTemplateRepository.kt" }!!
    assertSameLinesWithFile(
      "$testDataPath/file-template-result/repository/FileTemplateRepository.kt",
      repository.text
    )
    assertEquals(
      "FileTemplateRepository",
      props.getProperty(PROPS_SIMPLE_NAME("Repository"))
    )
    assertEquals(
      "com.github.rougsig.filetemplateloader.FileTemplateRepository",
      props.getProperty(PROPS_CLASS_NAME("Repository"))
    )

    val repositoryImpl = group.find { it.name == "FileTemplateRepositoryImpl.kt" }!!
    assertSameLinesWithFile(
      "$testDataPath/file-template-result/repository/FileTemplateRepositoryImpl.kt",
      repositoryImpl.text
    )
    assertEquals(
      "FileTemplateRepositoryImpl",
      props.getProperty(PROPS_SIMPLE_NAME("RepositoryImpl"))
    )
    assertEquals(
      "com.github.rougsig.filetemplateloader.FileTemplateRepositoryImpl",
      props.getProperty(PROPS_CLASS_NAME("RepositoryImpl"))
    )

    val repositoryBindings = group.find { it.name == "FileTemplateRepositoryBindings.kt" }!!
    assertSameLinesWithFile(
      "$testDataPath/file-template-result/repository/di/FileTemplateRepositoryBindings.kt",
      repositoryBindings.text
    )
    TestCase.assertEquals(
      "di",
      repositoryBindings.parent!!.name
    )
    assertEquals(
      "FileTemplateRepositoryBindings",
      props.getProperty(PROPS_SIMPLE_NAME("RepositoryBindings"))
    )
    assertEquals(
      "com.github.rougsig.filetemplateloader.di.FileTemplateRepositoryBindings",
      props.getProperty(PROPS_CLASS_NAME("RepositoryBindings"))
    )
  }
}
