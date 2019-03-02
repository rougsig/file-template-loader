package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.generator.Props
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.core.getFqNameByDirectory

class FileTemplateCreatorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("", "")
  }

  private fun doTest(testFileName: String, props: Props = DEFAULT_PROPS) {
    val fileName = testFileName.replace(".ft", "")
    val template = project.guessProjectDir()!!.readFileTemplate(testFileName)
    val dir = psiManager.findDirectory(project.guessProjectDir()!!)!!

    props.setProperty(PROP_FILE_NAME, fileName)
    val generatedProps = template.generateProps(props)

    val createdFileTemplateFiles = project.writeAction {
      template
        .create(dir, generatedProps)
    }

    createdFileTemplateFiles.forEach { createdFileTemplateFile ->

      val filePathBuilder = StringBuilder()
      filePathBuilder.append("$testDataPath/fileTemplateCreator/")
      val fileDirectory = createdFileTemplateFile.getFqNameByDirectory().asString()
      if (fileDirectory.isNotBlank()) filePathBuilder.append("$fileDirectory/")
      filePathBuilder.append("${createdFileTemplateFile.name}.txt")

      assertSameLinesWithFile(
        filePathBuilder.toString(),
        createdFileTemplateFile.text
      )
    }
  }

  fun testGitignoreFt() = doTest(".gitignore.ft")

  fun testGitignoreTemplateJson() = doTest(".gitignore.template.json")

  fun testRepositoryTemplateJson() = doTest("Repository.template.json")

  fun testRepositoryGroupJson() = doTest("Repository.group.json")
}
