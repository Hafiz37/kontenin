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

    // ── Conflict Dialog Integration Tests (FE-12) ───────────────────────────

    private fun fillValidForm(
        tanggalEpochDay: Long = 20000L,
        jamMulaiMenit: Int = 600,
        durasiText: String = "2"
    ) {
        viewModel.onNamaPengantin1Change("Andi")
        viewModel.onNamaPengantin2Change("Sari")
        viewModel.onTanggalSelected(tanggalEpochDay)
        viewModel.onJamMulaiSelected(jamMulaiMenit)
        viewModel.onDurasiChange(durasiText)
        viewModel.onAcaraChange("Resepsi")
        viewModel.onSumberJobChange("Vendor A")
        viewModel.onFeeChange("2000000")
    }

    @Test
    fun `job 10_00-14_00 bertemu 14_00-16_00 tidak memicu dialog bentrok dan langsung tersimpan`() = runBlocking {
        val tanggal = 20000L
        // Seed job 10.00 - 14.00 (600, durasi 240)
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Budi",
                namaPengantin2 = "Rina",
                tanggal = tanggal,
                jamMulaiMenit = 600,
                durasiMenit = 240,
                acara = "Akad",
                sumberJob = "Vendor",
                fee = 1000000L
            )
        )

        // Isi form job baru mulai 14.00 durasi 2 jam (840, durasi 120 -> 14.00 - 16.00)
        fillValidForm(tanggalEpochDay = tanggal, jamMulaiMenit = 840, durasiText = "2")

        viewModel.onSaveClick()

        assertFalse(viewModel.uiState.value.showConflictDialog)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `job mulai 13_59 tumpang tindih dengan job 10_00-14_00 memicu dialog bentrok`() = runBlocking {
        val tanggal = 20000L
        // Seed job 10.00 - 14.00
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Budi",
                namaPengantin2 = "Rina",
                tanggal = tanggal,
                jamMulaiMenit = 600,
                durasiMenit = 240,
                acara = "Akad",
                sumberJob = "Vendor",
                fee = 1000000L
            )
        )

        // Isi form job baru mulai 13.59 (839)
        fillValidForm(tanggalEpochDay = tanggal, jamMulaiMenit = 839, durasiText = "2")

        viewModel.onSaveClick()

        assertTrue(viewModel.uiState.value.showConflictDialog)
        assertEquals(1, viewModel.uiState.value.conflictingJobs.size)
        assertFalse(viewModel.uiState.value.isSaved)
    }

    @Test
    fun `job 22_00-02_00 bertemu job hari berikut pukul 01_00 memicu dialog bentrok`() = runBlocking {
        val hari1 = 20000L
        val hari2 = 20001L

        // Seed job lintas hari di hari1: 22.00 - 02.00 (1320, durasi 240)
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Joko",
                namaPengantin2 = "Siti",
                tanggal = hari1,
                jamMulaiMenit = 1320,
                durasiMenit = 240,
                acara = "Resepsi Malam",
                sumberJob = "Pribadi",
                fee = 3000000L
            )
        )

        // Job baru di hari2: pukul 01.00 (60)
        fillValidForm(tanggalEpochDay = hari2, jamMulaiMenit = 60, durasiText = "2")

        viewModel.onSaveClick()

        assertTrue(viewModel.uiState.value.showConflictDialog)
        assertEquals(1, viewModel.uiState.value.conflictingJobs.size)
    }

    @Test
    fun `dialog bentrok aksi Kembali mempertahankan draft tanpa simpan`() = runBlocking {
        val tanggal = 20000L
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Budi",
                namaPengantin2 = "Rina",
                tanggal = tanggal,
                jamMulaiMenit = 600,
                durasiMenit = 240,
                acara = "Akad",
                sumberJob = "Vendor",
                fee = 1000000L
            )
        )

        fillValidForm(tanggalEpochDay = tanggal, jamMulaiMenit = 700, durasiText = "2")
        viewModel.onSaveClick()
        assertTrue(viewModel.uiState.value.showConflictDialog)

        // Klik Kembali
        viewModel.onConflictDismiss()

        assertFalse(viewModel.uiState.value.showConflictDialog)
        assertFalse(viewModel.uiState.value.isSaved)
        // Draft tetap utuh
        assertEquals("Andi", viewModel.uiState.value.namaPengantin1)
        assertEquals(700, viewModel.uiState.value.jamMulaiMenit)
    }

    @Test
    fun `dialog bentrok aksi Tetap Simpan menyimpan job tepat satu kali`() = runBlocking {
        val tanggal = 20000L
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Budi",
                namaPengantin2 = "Rina",
                tanggal = tanggal,
                jamMulaiMenit = 600,
                durasiMenit = 240,
                acara = "Akad",
                sumberJob = "Vendor",
                fee = 1000000L
            )
        )

        val initialJobCount = gateway.observeAllJobs().first().size

        fillValidForm(tanggalEpochDay = tanggal, jamMulaiMenit = 700, durasiText = "2")
        viewModel.onSaveClick()
        assertTrue(viewModel.uiState.value.showConflictDialog)

        // Klik Tetap Simpan
        viewModel.onConflictForceSave()

        assertFalse(viewModel.uiState.value.showConflictDialog)
        assertTrue(viewModel.uiState.value.isSaved)

        val updatedJobCount = gateway.observeAllJobs().first().size
        assertEquals(initialJobCount + 1, updatedJobCount)
    }

    @Test
    fun `ubah job dengan jadwal yang sama tidak bentrok dengan dirinya sendiri`() = runBlocking {
        val tanggal = 20000L
        val savedResult = gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Doni",
                namaPengantin2 = "Maya",
                tanggal = tanggal,
                jamMulaiMenit = 600,
                durasiMenit = 120,
                acara = "Akad",
                sumberJob = "Pribadi",
                fee = 2000000L
            )
        )
        val savedJob = (savedResult as com.example.kontenin.contract.JobResult.Success).job

        // Buat ViewModel dalam mode Edit dengan jobId
        val editViewModel = JobFormViewModel(
            jobGateway = gateway,
            savedStateHandle = androidx.lifecycle.SavedStateHandle(),
            jobId = savedJob.id,
            mode = Routes.MODE_EDIT,
            dispatcher = kotlinx.coroutines.Dispatchers.Unconfined
        )

        // Form terisi dengan jadwal yang sama
        editViewModel.onSaveClick()

        assertFalse(editViewModel.uiState.value.showConflictDialog)
        assertTrue(editViewModel.uiState.value.isSaved)
    }
}
