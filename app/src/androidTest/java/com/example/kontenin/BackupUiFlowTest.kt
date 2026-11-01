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

/**
 * Backup UI Flow Test (FE-19).
 *
 * Menguji aksi dan dialog ekspor serta impor data cadangan simulasi di Pengaturan:
 * 1. Tombol Impor Data dan Ekspor Data ada dan interaktif
 * 2. Ketuk Ekspor membuka dialog pilihan tujuan ekspor
 * 3. Ketuk Impor membuka pemilih berkas cadangan demo
 */
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

    @Test fun exportButtonOpensDestinationDialog() {
        compose.onNode(hasText("Pengaturan") and isSelectable()).performClick()
        compose.onNodeWithText("Ekspor Data", ignoreCase = true).performClick()
        compose.onNodeWithText("Ekspor Cadangan").assertIsDisplayed()
        compose.onNodeWithText("Batal").performClick()
    }

    @Test fun importButtonOpensFixtureChooserDialog() {
        compose.onNode(hasText("Pengaturan") and isSelectable()).performClick()
        compose.onNodeWithText("Impor Data", ignoreCase = true).performClick()
        compose.onNodeWithText("Pilih Data Cadangan").assertIsDisplayed()
        compose.onNodeWithText("Batal").performClick()
    }
}
