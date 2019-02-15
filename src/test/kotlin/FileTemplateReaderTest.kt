package com.github.rougsig.filetemplateloader

import com.google.gson.Gson
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase

class FileTemplateReaderTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testReadFileTemplates() {
    val templateDirectory = myFixture.copyDirectoryToProject("file-template-reader", "")

    val templates = readFileTemplates(templateDirectory)

    assertSameElements(
      templates,
      listOf(
        FileTemplate(
          name = "Repository",
          extension = "kt",
          text = ""
        ),
        FileTemplate(
          name = "RepositoryImpl",
          extension = "kt",
          text = ""
        ),
        FileTemplate(
          name = "RepositoryBindings",
          extension = "kt",
          text = ""
        )
      )
    )
  }

  fun testReadFileTemplateGroups() {
    val templateDirectory = myFixture.copyDirectoryToProject("file-template-reader", "")
    val gson = Gson()

    val templates = readFileTemplates(templateDirectory)
    val groups = readFileTemplateGroups(templates, templateDirectory, gson)

    assertSameElements(
      groups,
      listOf(
        FileTemplateGroup(
          name = "Repository",
          templates = listOf(
            FileTemplate(
              name = "\${FLOW_NAME}Repository",
              extension = "kt",
              text = ""
            ),
            FileTemplate(
              name = "\${FLOW_NAME}RepositoryImpl",
              extension = "kt",
              text = ""
            ),
            FileTemplate(
              name = "\${FLOW_NAME}RepositoryBindings",
              extension = "kt",
              text = "",
              directory = "di"
            )
          )
        )
      )
    )
  }
}
