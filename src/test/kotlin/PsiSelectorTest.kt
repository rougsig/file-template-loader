package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.selector.select
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.plugins.groovy.GroovyFileType

class PsiSelectorTest : LightPlatformCodeInsightFixtureTestCase() {
  override fun getTestDataPath(): String = calculateTestDataPath()

  fun testSimpleSelect() {
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(GroovyFileType.GROOVY_FILE_TYPE, "*.gradle")
    }

    myFixture.copyDirectoryToProject("file-template-selector", "")

    val settingsGradleFile = project.guessProjectDir()!!.findChild("settings.gradle")!!
    val settingsGradle = myFixture.psiManager.findFile(settingsGradleFile)!!

    val selected = settingsGradle.select("CALL COMMAND LITERAL")!!

    assertEquals(
      "':jwt-ui-routing-otp'",
      selected.text
    )
  }

  fun testKotlinWhenSelect() {
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(KotlinFileType.INSTANCE, "*.kt")
    }

    myFixture.copyDirectoryToProject("file-template-selector", "")

    val whenSelectFile = project.guessProjectDir()!!.findFileByRelativePath("src/kotlin/ScreenFactory.kt")!!
    val whenSelect = myFixture.psiManager.findFile(whenSelectFile)!!

    val selected = whenSelect.select("fun+invoke WHEN_ENTRY")!!

    assertEquals(
      "is Route.Key3 -> ScreenKey3()",
      selected.text
    )
  }

  fun testScreenKeyFactoryInsert() {
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(KotlinFileType.INSTANCE, "*.kt")
    }

    myFixture.copyDirectoryToProject("file-template-selector", "")

    val whenSelectFile = project.guessProjectDir()!!.findFileByRelativePath("src/kotlin/ScreenFactory.kt")!!
    val whenSelectFileResult = project.guessProjectDir()!!.findFileByRelativePath("src/kotlin/ScreenFactoryResult.kt")!!
    val whenSelectResult = myFixture.psiManager.findFile(whenSelectFileResult)!!
    val whenSelect = myFixture.psiManager.findFile(whenSelectFile)!!

    val selected = whenSelect.select("fun+invoke WHEN_ENTRY")!!

    val factory = KtPsiFactory(selected)
    val exp = factory.createWhenEntry("is Route.Key4 -> ScreenKey4()")
    project.writeAction {
      selected.navigationElement.add(exp)
    }
    project.writeAction {
      CodeStyleManager.getInstance(project).reformat(whenSelectResult)
      CodeStyleManager.getInstance(project).reformat(whenSelect)
    }
    assertSameLines(
      whenSelectResult.text,
      whenSelect.text
    )
  }
}
