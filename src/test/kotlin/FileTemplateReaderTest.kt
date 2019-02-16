package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.FileTemplateGroup
import com.github.rougsig.filetemplateloader.entity.FileTemplateSingle
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplates
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
        FileTemplateSingle(
          name = "Repository",
          fileName = "Repository",
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryImpl",
          fileName = "RepositoryImpl",
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryBindings",
          fileName = "RepositoryBindings",
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

    assertEquals(
      groups,
      listOf(
        FileTemplateGroup(
          name = "Repository",
          templates = listOf(
            FileTemplateSingle(
              name = "Repository",
              fileName = "\${FLOW_NAME}Repository",
              extension = "kt",
              text = ""
            ),
            FileTemplateSingle(
              name = "RepositoryImpl",
              fileName = "\${FLOW_NAME}RepositoryImpl",
              extension = "kt",
              text = ""
            ),
            FileTemplateSingle(
              name = "RepositoryBindings",
              fileName = "\${FLOW_NAME}RepositoryBindings",
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
