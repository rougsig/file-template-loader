package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.entity.*
import com.github.rougsig.filetemplateloader.reader.readFileTemplateGroups
import com.github.rougsig.filetemplateloader.reader.readFileTemplateModules
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
          fileName = null,
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryImpl",
          fileName = null,
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryBindings",
          fileName = null,
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
          ),
          injectors = listOf(
            FileTemplateInjector(
              text = "is \${FLOW_NAME}Key -> \${FLOW_NAME}Screen()",
              className = "\${APP_ROUTE_CLASS_NAME}",
              selector = "CLASS INVOKE WHEN WHEN_ENTRY"
            )
          )
        )
      )
    )
  }

  fun testReadFileTemplateModule() {
    val templateDirectory = myFixture.copyDirectoryToProject("file-template-reader", "")
    val gson = Gson()

    val templates = readFileTemplates(templateDirectory)
    val modules = readFileTemplateModules(templates, templateDirectory, gson)

    assertEquals(
      modules,
      listOf(
        FileTemplateModule(
          name = "Repository",
          moduleName = "\${PROJECT_NAME_LOWER_KEBAB_CASE}-\${FLOW_NAME_LOWER_KEBAB_CASE}-repository",
          folders = listOf(
            FileTemplateFolder(
              pathName = "src/main/kotlin/repositories",
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
          ),
          injectors = listOf(
            FileTemplateInjector(
              text = "is \${FLOW_NAME}Key -> \${FLOW_NAME}Screen()",
              className = "\${APP_ROUTE_CLASS_NAME}",
              selector = "CLASS INVOKE WHEN WHEN_ENTRY"
            )
          )
        )
      )
    )
  }

  fun testProjectReadFileTemplates() {
    myFixture.copyDirectoryToProject("file-template-reader", "")

    val templates = project.readFileTemplates()

    assertSameElements(
      templates,
      listOf(
        FileTemplateSingle(
          name = "Repository",
          fileName = null,
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryImpl",
          fileName = null,
          extension = "kt",
          text = ""
        ),
        FileTemplateSingle(
          name = "RepositoryBindings",
          fileName = null,
          extension = "kt",
          text = ""
        )
      )
    )
  }

  fun testProjectReadFileTemplateGroups() {
    myFixture.copyDirectoryToProject("file-template-reader", "")
    val gson = Gson()

    val templates = project.readFileTemplates()
    val groups = project.readFileTemplateGroups(templates, gson)

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
          ),
          injectors = listOf(
            FileTemplateInjector(
              text = "is \${FLOW_NAME}Key -> \${FLOW_NAME}Screen()",
              className = "\${APP_ROUTE_CLASS_NAME}",
              selector = "CLASS INVOKE WHEN WHEN_ENTRY"
            )
          )
        )
      )
    )
  }

  fun testProjectReadFileTemplateModule() {
    myFixture.copyDirectoryToProject("file-template-reader", "")
    val gson = Gson()

    val templates = project.readFileTemplates()
    val modules = project.readFileTemplateModules(templates, gson)

    assertEquals(
      modules,
      listOf(
        FileTemplateModule(
          name = "Repository",
          moduleName = "\${PROJECT_NAME_LOWER_KEBAB_CASE}-\${FLOW_NAME_LOWER_KEBAB_CASE}-repository",
          folders = listOf(
            FileTemplateFolder(
              pathName = "src/main/kotlin/repositories",
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
          ),
          injectors = listOf(
            FileTemplateInjector(
              text = "is \${FLOW_NAME}Key -> \${FLOW_NAME}Screen()",
              className = "\${APP_ROUTE_CLASS_NAME}",
              selector = "CLASS INVOKE WHEN WHEN_ENTRY"
            )
          )
        )
      )
    )
  }
}
