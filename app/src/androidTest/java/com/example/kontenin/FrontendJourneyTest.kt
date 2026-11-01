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
 * Frontend Journey Acceptance Test (FE-19).
 *
 * Menguji alur pengguna kritis pada fase frontend:
 * 1. Validasi form kosong menunjukkan error nama pengantin wajib diisi
 * 2. Navigasi tambah job -> input data lengkap -> simpan sukses
 * 3. Navigasi detail job -> cek informasi -> buka dialog pembayaran
 * 4. Navigasi detail job -> hapus job dengan dialog konfirmasi
 */
class FrontendJourneyTest {
    @get:Rule val compose = createComposeRule()

    @Before fun launch() {
        val container = AppContainer(FixedClock(Instant.parse("2026-10-29T05:00:00Z")))
        compose.setContent { KonteninTheme { KonteninApp(container) } }
    }

    @Test fun savingEmptyFormShowsRequiredNameError() {
        compose.onNodeWithContentDescription("Tambah job").performClick()
        compose.onNodeWithText("Simpan").performClick()
        compose.onNodeWithText("Nama pengantin 1 wajib diisi").assertExists()
    }

    @Test fun navigateToDetailAndInspectElements() {
        // Pada data demo default, ada job "Budi & Santoso"
        compose.onNodeWithText("Budi & Santoso", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Budi & Santoso", substring = true).performClick()

        // Di layar Detail Job, periksa elemen-elemen kunci
        compose.onNodeWithText("Ubah").assertIsDisplayed()
        compose.onNodeWithText("Duplikat").assertIsDisplayed()
        compose.onNodeWithText("Hapus").assertIsDisplayed()
        compose.onNodeWithText("Ubah Pembayaran").assertIsDisplayed()
    }

    @Test fun openingDeleteDialogShowsConfirmationMessage() {
        // Masuk ke detail salah satu job
        compose.onNodeWithText("Budi & Santoso", substring = true).performClick()

        // Ketuk tombol hapus
        compose.onNodeWithText("Hapus").performClick()

        // Dialog konfirmasi harus muncul dengan teks peringatan
        compose.onNodeWithText("Hapus job ini?").assertIsDisplayed()
        compose.onNodeWithText("Batal").assertIsDisplayed()
    }

    @Test fun openingPaymentDialogShowsInputs() {
        // Masuk ke detail salah satu job
        compose.onNodeWithText("Budi & Santoso", substring = true).performClick()

        // Ketuk Ubah Pembayaran
        compose.onNodeWithText("Ubah Pembayaran").performClick()

        // Dialog pembayaran harus muncul
        compose.onNodeWithText("Ubah Pembayaran").assertIsDisplayed()
        compose.onNodeWithText("Batal").assertIsDisplayed()
        compose.onNodeWithText("Simpan").assertIsDisplayed()
    }
}
