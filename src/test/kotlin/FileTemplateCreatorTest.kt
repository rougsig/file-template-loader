package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.generator.setProperty
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.core.getFqNameByDirectory
import org.jetbrains.plugins.groovy.GroovyFileType

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("testProject", "")

    project.writeAction {
      FileTypeManager.getInstance().associatePattern(GroovyFileType.GROOVY_FILE_TYPE, "*.gradle")
      FileTypeManager.getInstance().associatePattern(PlainTextFileType.INSTANCE, ".gitignore")
    }
  }

  private fun doTest(testFileName: String, subDir: String) {
    val fileName = testFileName.replace(".ft", "")
    val template = project.readFileTemplate(testFileName)
    val props = project.readConfig()
      .apply { DEFAULT_PROPS.forEach { (k, v) -> setProperty(k, v) } }
    val dir = psiManager.findDirectory(project.guessProjectDir()!!)!!

    props.setProperty(PROP_FILE_NAME, fileName)
    val generatedProps = template.generateProps(props)

    val createdFileTemplateFiles = project.writeAction {
      template
        .create(dir, generatedProps)
    }

    createdFileTemplateFiles.forEach { createdFileTemplateFile ->

      val filePathBuilder = StringBuilder()
      filePathBuilder.append("$testDataPath/fileTemplateCreator/$subDir/")
      val fileDirectory = createdFileTemplateFile.getFqNameByDirectory().asString()
      if (fileDirectory.isNotBlank()) filePathBuilder.append("$fileDirectory/")
      filePathBuilder.append("${createdFileTemplateFile.name}.txt")

      assertSameLinesWithFile(
        filePathBuilder.toString(),
        createdFileTemplateFile.text
      )
    }
  }

  fun testGitignoreFt() = doTest(".gitignore.ft", "gitignore")

  fun testDummyKtFt() = doTest("Dummy.kt.ft", "Dummy")

  fun testDomainGroupJson() = doTest("DomainGroup.json", "DomainGroup")
  fun testDomainModuleJson() = doTest("DomainModule.json", "DomainModule")

  fun testMviScreenGroupJson() = doTest("MviScreenGroup.json", "MviScreenGroup")
  fun testMviScreenModuleJson() = doTest("MviScreenModule.json", "MviScreenModule")

  fun testRepositoryGroupJson() = doTest("RepositoryGroup.json", "RepositoryGroup")
  fun testRepositoryModuleGroupJson() = doTest("RepositoryModule.json", "RepositoryModule")

  fun testRoutingFlowGroupJson() = doTest("RoutingFlow.group.json", "RoutingFlowGroup")
}
