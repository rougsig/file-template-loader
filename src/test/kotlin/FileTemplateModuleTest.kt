package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.Props
import com.github.rougsig.filetemplateloader.extension.calculatePackageName
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplateModules
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
import com.google.gson.Gson
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.util.sourceRoots

class FileTemplateModuleTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testCalculatePackageName() {
    myFixture.copyDirectoryToProject("file-template-creator", "")

    val config = project.readConfig()

    val src = myModule.sourceRoots.first()
    val dir = psiManager.findDirectory(src)!!

    val packageName = dir.calculatePackageName(config)

    assertEquals("com.github.rougsig.light.idea.test.case", packageName)
  }

  fun testCreateFileTemplateModule() {
    val projectDirectory = myFixture.copyDirectoryToProject("file-template-creator", "")
    val fileTemplateDirectory = projectDirectory.findChild(".fileTemplates")!!

    val templates = readFileTemplates(fileTemplateDirectory)
    val templateGroups = readFileTemplateModules(templates, fileTemplateDirectory, Gson())
    val config = project.readConfig()

    val src = psiManager.findDirectory(myModule.sourceRoots.first())!!

    val viewFileTemplateGroup = templateGroups.find { it.name == "Repository" }!!

    val props = Props(config)
    props.setProperty("FLOW_NAME", "Epic")
    viewFileTemplateGroup.generateProps(props)

    val module = project.writeAction {
      viewFileTemplateGroup.create(src, props)
    }

    val repository = module.find { it.name == "EpicRepository.kt" }!!
    assertFileTemplate(
      "module/EpicRepository.kt",
      "Repository",
      "\\filetemplateloader-epic-repository\\src\\main\\kotlin\\repositories",
      props,
      repository,
      "EpicRepository",
      "com.github.rougsig.filetemplateloader.epic.repository.EpicRepository"
    )

    val repositoryImpl = module.find { it.name == "EpicRepositoryImpl.kt" }!!
    assertFileTemplate(
      "module/EpicRepositoryImpl.kt",
      "RepositoryImpl",
      "\\filetemplateloader-epic-repository\\src\\main\\kotlin\\repositories",
      props,
      repositoryImpl,
      "EpicRepositoryImpl",
      "com.github.rougsig.filetemplateloader.epic.repository.EpicRepositoryImpl"
    )

    val repositoryBindings = module.find { it.name == "EpicRepositoryBindings.kt" }!!
    assertFileTemplate(
      "module/di/EpicRepositoryBindings.kt",
      "RepositoryBindings",
      "\\filetemplateloader-epic-repository\\src\\main\\kotlin\\repositories\\di",
      props,
      repositoryBindings,
      "EpicRepositoryBindings",
      "com.github.rougsig.filetemplateloader.epic.repository.di.EpicRepositoryBindings"
    )
  }
}
