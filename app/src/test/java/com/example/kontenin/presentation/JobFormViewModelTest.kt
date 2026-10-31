package com.example.kontenin.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.model.JobDraft
import com.example.kontenin.presentation.jobform.FormValidationError
import com.example.kontenin.presentation.jobform.JobFormField
import com.example.kontenin.presentation.jobform.JobFormViewModel
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Unit test untuk interaksi JobFormViewModel (FE-09).
 */
class JobFormViewModelTest {

    private lateinit var gateway: DemoJobGateway
    private lateinit var viewModel: JobFormViewModel

    @Before
    fun setUp() {
        val clock = FixedClock(Instant.parse("2026-10-31T08:00:00Z"))
        gateway = DemoJobGateway(clock)
        viewModel = JobFormViewModel(
            jobGateway = gateway,
            savedStateHandle = SavedStateHandle(),
            jobId = 0L,
            mode = Routes.MODE_ADD,
            dispatcher = kotlinx.coroutines.Dispatchers.Unconfined
        )
    }

    @Test
    fun `simpan form kosong tidak menyimpan dan fokus menuju error pertama nama pengantin 1`() {
        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertFalse(state.errors.isEmpty())
        assertTrue(state.errors.containsKey(JobFormField.NAMA_PENGANTIN_1))
        assertEquals(JobFormField.NAMA_PENGANTIN_1, state.scrollToField)

        // Reset scroll trigger
        viewModel.onScrollToFieldConsumed()
        assertNull(viewModel.uiState.value.scrollToField)
    }

    @Test
    fun `lunas hanya aktif saat fee valid dan lebih dari 0`() {
        assertFalse(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("0")
        assertFalse(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("1000000")
        assertTrue(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("")
        assertFalse(viewModel.uiState.value.isLunasEnabled)
    }

    @Test
    fun `klik Lunas mengisi dibayar sama dengan fee dan menurunkan fee memunculkan error`() {
        // Set fee 1.000.000 dan dibayar 1.100.000
        viewModel.onFeeChange("1000000")
        viewModel.onDibayarChange("1100000")

        // Memunculkan error dibayar
        val stateWithExceed = viewModel.uiState.value
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            stateWithExceed.errors[JobFormField.DIBAYAR]
        )

        // Klik Lunas: dibayar = 1.000.000
        viewModel.onLunasClick()
        val stateAfterLunas = viewModel.uiState.value
        assertEquals("1000000", stateAfterLunas.dibayarText)
        assertNull(stateAfterLunas.errors[JobFormField.DIBAYAR])

        // Turunkan fee ke 800.000: error dibayar muncul kembali
        viewModel.onFeeChange("800000")
        val stateAfterFeeLowered = viewModel.uiState.value
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            stateAfterFeeLowered.errors[JobFormField.DIBAYAR]
        )
    }

    @Test
    fun `validasi saat blur dan perbaikan input seketika menghilangkan error`() {
        // Nama diisi > 50 karakter lalu blur
        val longName = "A".repeat(55)
        viewModel.onNamaPengantin1Change(longName)
        viewModel.onFieldBlur(JobFormField.NAMA_PENGANTIN_1)

        assertEquals(
            FormValidationError.NamaPengantin1TooLong,
            viewModel.uiState.value.errors[JobFormField.NAMA_PENGANTIN_1]
        )

        // Perbaiki input menjadi valid
        viewModel.onNamaPengantin1Change("Andi")
        assertNull(viewModel.uiState.value.errors[JobFormField.NAMA_PENGANTIN_1])
    }

    // ── Autocomplete Integration Tests (FE-10) ──────────────────────────────

    @Test
    fun `ketik resepsi dan pilih saran lalu edit menjadi teks baru tetap diperbolehkan`() = runBlocking {
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Andi",
                namaPengantin2 = "Sari",
                tanggal = 1000L,
                jamMulaiMenit = 600,
                durasiMenit = 120,
                acara = "Resepsi",
                alamat = "Gedung A",
                shareloc = "",
                sumberJob = "Pribadi",
                fee = 1000000L,
                dibayar = 0L,
                catatan = ""
            )
        )

        // Query "resep" memunculkan saran "Resepsi"
        viewModel.onAcaraChange("resep")
        val state = viewModel.uiState.value
        assertTrue(state.showAcaraSuggestions)
        assertTrue(state.acaraSuggestions.contains("Resepsi"))

        // Pilih saran "Resepsi"
        viewModel.onAcaraSuggestionSelected("Resepsi")
        assertEquals("Resepsi", viewModel.uiState.value.acara)
        assertFalse(viewModel.uiState.value.showAcaraSuggestions)

        // Edit kembali menjadi teks baru bebas
        viewModel.onAcaraChange("Resepsi & Akad")
        assertEquals("Resepsi & Akad", viewModel.uiState.value.acara)
    }

    @Test
    fun `simpan sumber dengan spasi berlebih dan kapital berbeda memakai ejaan lama`() = runBlocking {
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Andi",
                namaPengantin2 = "Sari",
                tanggal = 1000L,
                jamMulaiMenit = 600,
                durasiMenit = 120,
                acara = "Resepsi",
                alamat = "Gedung A",
                shareloc = "",
                sumberJob = "Pribadi",
                fee = 1000000L,
                dibayar = 0L,
                catatan = ""
            )
        )

        // "   pribadi   " dengan data existing yang memuat "Pribadi"
        viewModel.onSumberJobChange("   pribadi   ")

        // Saat simpan, normalisasi dipicu
        viewModel.onSaveClick()

        assertEquals("Pribadi", viewModel.uiState.value.sumberJob)
    }

    @Test
    fun `fokus field memicu pembaruan saran autocomplete maksimal 5 item`() {
        viewModel.onAcaraFocus()
        val suggestions = viewModel.uiState.value.acaraSuggestions
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.size <= 5)
        assertTrue(viewModel.uiState.value.showAcaraSuggestions)
    }

    @Test
    fun `hapus job demo memperbarui daftar saran autocomplete secara reaktif`() = runBlocking {
        viewModel.onAcaraFocus()
        val initialJobs = gateway.observeAllJobs().first()
        val firstJob = initialJobs.first()

        // Hapus job pertama dari gateway
        gateway.deleteJob(firstJob.id)

        // State existing list di ViewModel langsung mengikuti data terbaru dari gateway
        val updatedJobs = gateway.observeAllJobs().first()
        val expectedAcaraList = updatedJobs.map { it.acara }
        assertEquals(expectedAcaraList, viewModel.uiState.value.existingAcaraList)
    }
}
