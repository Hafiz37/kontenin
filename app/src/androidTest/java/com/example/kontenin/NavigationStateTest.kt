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

/** Fresh container and fixed WIB clock for every scenario; never uses device time. */
class NavigationStateTest {
    @get:Rule val compose = createComposeRule()

    @Before fun launch() {
        val container = AppContainer(FixedClock(Instant.parse("2026-10-29T05:00:00Z")))
        compose.setContent { KonteninTheme { KonteninApp(container) } }
    }

    private fun tab(label: String) = compose.onNode(hasText(label) and isSelectable())

    @Test fun mainTabsAndRecapPeriodAreRestored() {
        tab("Rekap").performClick()
        compose.onNodeWithText("Minggu Ini").performClick().assertIsSelected()
        tab("Pengaturan").performClick().assertIsSelected()
        tab("Rekap").performClick()
        compose.onNodeWithText("Minggu Ini").assertIsSelected()
        tab("Beranda").performClick().assertIsSelected()
    }

    @Test fun emptyFormHidesBottomNavigationAndCloseReturnsHome() {
        compose.onNodeWithContentDescription("Tambah job").performClick()
        tab("Beranda").assertDoesNotExist()
        compose.onNodeWithText("Simpan").assertIsDisplayed()
        compose.onNodeWithContentDescription("Tutup").performClick()
        tab("Beranda").assertIsSelected()
        compose.onNodeWithContentDescription("Tambah job").assertIsDisplayed()
    }

    @Test fun closingChangedDraftRequiresDiscardConfirmation() {
        compose.onNodeWithContentDescription("Tambah job").performClick()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Andi")
        compose.onNodeWithContentDescription("Tutup").performClick()
        compose.onNodeWithText("Buang perubahan?").assertIsDisplayed()
    }

}
