package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.constant.PROJECT_NAME
import com.github.rougsig.filetemplateloader.reader.readConfig
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import java.util.*

class ConfigReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testReadConfig() {
    val templateDirectory = myFixture.copyDirectoryToProject("file-template-reader", "")

    val config = readConfig(templateDirectory)

    assertEquals(
      Properties().apply {
        setProperty(PROJECT_NAME, "file-template-loader")
      },
      config
    )
  }
}