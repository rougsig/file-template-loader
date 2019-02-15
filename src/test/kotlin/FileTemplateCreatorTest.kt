package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.creator.create
import com.github.rougsig.filetemplateloader.extension.calculatePackageName
import com.github.rougsig.filetemplateloader.extension.createSubDirs
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.google.gson.Gson
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

    val repositoryFileTemplate = templates.find { it.fileName == "Repository" }!!

    val props = Properties(config)
    props.setProperty(PROPS_NAME, "FileTemplateRepository")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val template = project.writeAction {
      repositoryFileTemplate.create(dir, props)
    }
    assertFileTemplate(
      "repository/FileTemplateRepository.kt",
      "Repository",
      "",
      props,
      template,
      "FileTemplateRepository",
      "com.github.rougsig.filetemplateloader.FileTemplateRepository"
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
    assertFileTemplate(
      "repository/FileTemplateRepository.kt",
      "Repository",
      "",
      props,
      repository,
      "FileTemplateRepository",
      "com.github.rougsig.filetemplateloader.FileTemplateRepository"
    )

    val repositoryImpl = group.find { it.name == "FileTemplateRepositoryImpl.kt" }!!
    assertFileTemplate(
      "repository/FileTemplateRepositoryImpl.kt",
      "RepositoryImpl",
      "",
      props,
      repositoryImpl,
      "FileTemplateRepositoryImpl",
      "com.github.rougsig.filetemplateloader.FileTemplateRepositoryImpl"
    )

    val repositoryBindings = group.find { it.name == "FileTemplateRepositoryBindings.kt" }!!
    assertFileTemplate(
      "repository/di/FileTemplateRepositoryBindings.kt",
      "RepositoryBindings",
      "di",
      props,
      repositoryBindings,
      "FileTemplateRepositoryBindings",
      "com.github.rougsig.filetemplateloader.di.FileTemplateRepositoryBindings"
    )
  }

  fun testCreateFileTemplateGroupWithRootDirectory() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val templateGroups = readFileTemplateGroups(templates, fileTemplateDirectory, Gson())
    val config = readConfig(fileTemplateDirectory)

    val src = psiManager.findDirectory(myModule.sourceRoots.first())!!

    val viewFileTemplateGroup = templateGroups.find { it.name == "View" }!!

    val props = Properties(config)
    props.setProperty("VIEW_NAME", "FileTemplate")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val group = project.writeAction {
      val kotlin = src.createSubDirs("src/main/kotlin")
      viewFileTemplateGroup.create(kotlin, props)
    }

    val repositoryImpl = group.find { it.name == "FileTemplateView.kt" }!!
    assertFileTemplate(
      "repository/FileTemplateRepositoryImpl.kt",
      "RepositoryImpl",
      "",
      props,
      repositoryImpl,
      "FileTemplateRepositoryImpl",
      "com.github.rougsig.filetemplateloader.FileTemplateRepositoryImpl"
    )

    val repositoryBindings = group.find { it.name == "FileTemplateLayout.xml" }!!
    assertFileTemplate(
      "repository/di/FileTemplateRepositoryBindings.kt",
      "RepositoryBindings",
      "di",
      props,
      repositoryBindings,
      "FileTemplateRepositoryBindings",
      "com.github.rougsig.filetemplateloader.di.FileTemplateRepositoryBindings"
    )
  }
}
