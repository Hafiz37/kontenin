package com.example.kontenin

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.kontenin.ui.KonteninApp
import com.example.kontenin.ui.theme.KonteninTheme
import com.example.kontenin.util.FixedClock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

/** Acceptance gate: a placeholder label must not count as an import action. */
class BackupUiFlowTest {
    @get:Rule val compose = createComposeRule()

    @Before fun launch() {
        val container = AppContainer(FixedClock(Instant.parse("2026-10-29T05:00:00Z")))
        compose.setContent { KonteninTheme { KonteninApp(container) } }
    }

    @Test fun settingsExposesActionableImport() {
        compose.onNode(hasText("Pengaturan") and isSelectable()).performClick()
        compose.onNodeWithText("Impor Data", ignoreCase = true).assertHasClickAction()
    }
}
