package com.github.rougsig.filetemplateloader

import com.github.rougsig.filetemplateloader.extension.writeAction
import com.github.rougsig.filetemplateloader.selector.ElementSelector
import com.github.rougsig.filetemplateloader.selector.select
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
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

    val selected = settingsGradle.select("Call Command", ElementSelector.FirstChild)
    val index = selected.select("Literal", ElementSelector.Index(4))
    val first = selected.select("Literal", ElementSelector.FirstChild)
    val last = selected.select("Literal", ElementSelector.LastChild)

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
}
