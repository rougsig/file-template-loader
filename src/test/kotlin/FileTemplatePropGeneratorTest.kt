package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROP_FILE_NAME
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.generator.generateProps
import com.github.rougsig.filetemplateloader.generator.setProperty
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplatePropGeneratorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("testProject", "")
  }

  private fun doTest(testFileName: String) {
    val template = project.readFileTemplate(testFileName)
    val props = project.readConfig()
      .apply { DEFAULT_PROPS.forEach { (k, v) -> setProperty(k, v) } }

    if (template is FileTemplateSingle) {
      props.setProperty(PROP_FILE_NAME, testFileName.replace(".ft", ""))
    }

    val generatedProps = template
      .generateProps(props)
      .map { (key, value) -> "$key=$value" }
      .sorted()
      .joinToString("\n") { it }

    assertSameLinesWithFile(
      "$testDataPath/fileTemplatePropGenerator/$testFileName.txt",
      generatedProps
    )
  }

  fun testGitignoreFt() = doTest(".gitignore.ft")

  fun testDummyKtFt() = doTest("Dummy.kt.ft")

  fun testDomainGroupJson() = doTest("DomainGroup.json")
  fun testDomainModuleJson() = doTest("DomainModule.json")

  fun testMviScreenGroupJson() = doTest("MviScreenGroup.json")
  fun testMviScreenModuleJson() = doTest("MviScreenModule.json")

  fun testRepositoryGroupJson() = doTest("RepositoryGroup.json")
  fun testRepositoryModuleGroupJson() = doTest("RepositoryModule.json")

  fun testRoutingFlowGroupJson() = doTest("RoutingFlowGroup.json")
}
