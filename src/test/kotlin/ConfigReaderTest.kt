package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.reader.readConfig
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class ConfigReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  override fun setUp() {
    super.setUp()

    myFixture.copyDirectoryToProject("file-template-reader", "")
  }

  fun testReadConfig() {
    val config = project.readConfig()

    assertEquals(
      Properties().apply {
        setProperty("PROJECT_NAME", "file-template-loader")
        setProperty("PACKAGE_NAME_TEMPLATE", "\${PROJECT_NAME}")
      },
      config
    )
  }
}
