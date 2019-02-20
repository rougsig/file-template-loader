package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROPS_FILE_NAME
import com.github.rougsig.filetemplateloader.constant.PROPS_PACKAGE_NAME
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.util.sourceRoots
import java.util.*

class FileTemplateSingleTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testCreateFileTemplateSingle() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val config = project.readConfig()

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val repositoryFileTemplate = templates.find { it.name == "Repository" }!!

    val props = Properties(config)
    props.setProperty(PROPS_FILE_NAME, "FileTemplateRepository")
    props.setProperty(PROPS_PACKAGE_NAME, "com.github.rougsig.filetemplateloader")
    val template = project.writeAction {
      repositoryFileTemplate.create(dir, props)
    }.first()
    assertFileTemplate(
      "repository/FileTemplateRepository.kt",
      "Repository",
      "",
      props,
      template,
      "",
      ""
    )
  }
}
