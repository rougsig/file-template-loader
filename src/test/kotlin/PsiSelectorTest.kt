package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.selector.ElementSelector
import com.github.rougsig.filetemplateloader.selector.select
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.KotlinFileType
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

    val selected = settingsGradle.select("CALL COMMAND", ElementSelector.FirstChild)
    val index = selected.select("LITERAL", ElementSelector.Index(4))
    val first = selected.select("LITERAL", ElementSelector.FirstChild)
    val last = selected.select("LITERAL", ElementSelector.LastChild)

    assertEquals(
      "':lib-domain-core'",
      index.text
    )
    assertEquals(
      "':app'",
      first.text
    )
    assertEquals(
      "':jwt-ui-routing-otp'",
      last.text
    )
  }

  fun testKotlinWhenSelect() {
    project.writeAction {
      FileTypeManager.getInstance().associatePattern(KotlinFileType.INSTANCE, "*.kt")
    }

    myFixture.copyDirectoryToProject("file-template-selector", "")

    val whenSelectFile = project.guessProjectDir()!!.findChild("WhenSelect.kt")!!
    val whenSelect = myFixture.psiManager.findFile(whenSelectFile)!!

    val selected = whenSelect.select("CLASS CLASS_BODY FUN BLOCK RETURN WHEN WHEN_ENTRY", ElementSelector.LastChild)

    assertEquals(
      "is Route.Key3 -> ScreenKey3()",
      selected.text
    )
  }
}
