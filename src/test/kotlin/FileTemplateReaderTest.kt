package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.readFileTemplate
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  private val gson = createUnitTestGson()

  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()
    myFixture.copyDirectoryToProject("testProject", "")
  }

  private fun doTest(testFileName: String) {
    val template = project.readFileTemplate(testFileName)
    val json = gson.toJson(template)

    assertSameLinesWithFile(
      "$testDataPath/fileTemplateReader/$testFileName.txt",
      json
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
