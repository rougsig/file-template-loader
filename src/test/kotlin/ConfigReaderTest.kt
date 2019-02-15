package com.github.rougsig.filetemplateloader

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class ConfigReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testReadConfig() {
    val templateDirectory = myFixture.copyDirectoryToProject("file-template-reader", "")

    val config = readConfig(templateDirectory)

    assertEquals(
      Properties().apply {
        setProperty("MODULE_PREFIX", "prefix")
      },
      config
    )
  }
}
