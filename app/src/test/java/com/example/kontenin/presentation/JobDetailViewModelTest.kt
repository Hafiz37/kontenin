package com.example.kontenin.presentation

import com.example.kontenin.contract.JobResult
import com.example.kontenin.demo.DemoExternalActionGateway
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.demo.DemoScenario
import com.example.kontenin.model.JobDraft
import com.example.kontenin.presentation.jobdetail.JobDetailViewModel
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class JobDetailViewModelTest {

    private val fixedClock = FixedClock(Instant.parse("2026-10-18T03:00:00Z"))

    private lateinit var jobGateway: DemoJobGateway
    private lateinit var externalActionGateway: DemoExternalActionGateway

    @Before
    fun setUp() {
        jobGateway = DemoJobGateway(clock = fixedClock)
        externalActionGateway = DemoExternalActionGateway()
    }

    @Test
    fun `load existing job populates all UI state fields correctly`() = runBlocking {
        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600, // 10.00
            durasiMenit = 240,   // 4 jam -> 14.00
            acara = "Resepsi",
            alamat = "Gedung Harmoni, Jl. Mawar No. 1",
            shareloc = "https://maps.app.goo.gl/sample",
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 2_000_000L,
            catatan = "Dress code putih",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val createdJob = saveResult.job

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = createdJob.id,
            dispatcher = Dispatchers.Unconfined,
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isJobNotFound)
        assertNotNull(state.job)
        assertEquals("Andi", state.job?.namaPengantin1)
        assertEquals("Sari", state.job?.namaPengantin2)
        assertEquals("Resepsi", state.job?.acara)
        assertEquals("Vendor A", state.job?.sumberJob)
        assertEquals("Dress code putih", state.job?.catatan)
        assertTrue(state.isMapsButtonVisible)

        val display = state.jobDisplay
        assertNotNull(display)
        assertEquals("Andi & Sari", display?.namaPasangan)
        assertEquals("10.00 - 14.00", display?.timeRangeFormatted)
        assertEquals("Rp 3.500.000", display?.feeFormatted)
        assertEquals("Rp 2.000.000", display?.dibayarFormatted)
        assertEquals("Rp 1.500.000", display?.sisaFormatted)
        assertEquals("Kurang Rp 1.500.000", display?.paymentChipText)
    }

    @Test
    fun `non existent job ID sets isJobNotFound to true`() = runBlocking {
        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = 999999L,
            dispatcher = Dispatchers.Unconfined,
        )

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isJobNotFound)
        assertNull(state.job)
        assertEquals("Job tidak ditemukan", state.userMessage)
    }

    @Test
    fun `empty or blank shareloc hides Maps button`() = runBlocking {
        val draft = JobDraft(
            namaPengantin1 = "Budi",
            namaPengantin2 = "Rina",
            tanggal = 20743L,
            jamMulaiMenit = 480,
            durasiMenit = 120,
            acara = "Akad",
            alamat = "KUA Sleman",
            shareloc = "   ",
            sumberJob = "Pribadi",
            fee = 1_000_000L,
            dibayar = 1_000_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = saveResult.job.id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertFalse(viewModel.uiState.value.isMapsButtonVisible)
    }

    @Test
    fun `invalid shareloc prefix hides Maps button`() = runBlocking {
        val draft = JobDraft(
            namaPengantin1 = "Budi",
            namaPengantin2 = "Rina",
            tanggal = 20743L,
            jamMulaiMenit = 480,
            durasiMenit = 120,
            acara = "Akad",
            alamat = "KUA Sleman",
            shareloc = "maps.google.com/sample",
            sumberJob = "Pribadi",
            fee = 1_000_000L,
            dibayar = 1_000_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = saveResult.job.id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertFalse(viewModel.uiState.value.isMapsButtonVisible)
    }

    @Test
    fun `valid shareloc records intent event in demo adapter when opened`() = runBlocking {
        val testUrl = "https://maps.app.goo.gl/sample123"
        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            acara = "Resepsi",
            alamat = "Gedung Harmoni",
            shareloc = testUrl,
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = saveResult.job.id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertTrue(viewModel.uiState.value.isMapsButtonVisible)

        viewModel.onOpenMaps()

        assertEquals(testUrl, externalActionGateway.lastOpenedMapsAddress)
        assertTrue(externalActionGateway.openedMapsEvents.contains(testUrl))
        assertTrue(viewModel.uiState.value.isMapsButtonVisible)
    }

    @Test
    fun `open maps failure shows PRD error message and hides button per D-11`() = runBlocking {
        val testUrl = "https://maps.app.goo.gl/broken"
        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            acara = "Resepsi",
            alamat = "Gedung Harmoni",
            shareloc = testUrl,
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success

        externalActionGateway.simulateOpenMapsError = true

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = saveResult.job.id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertTrue(viewModel.uiState.value.isMapsButtonVisible)

        viewModel.onOpenMaps()

        val state = viewModel.uiState.value
        assertFalse(state.isMapsButtonVisible)
        assertEquals("Tidak ada aplikasi untuk membuka link ini", state.userMessage)
    }

    @Test
    fun `delete cancellation leaves job unchanged in gateway`() = runBlocking {
        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            acara = "Resepsi",
            alamat = "Gedung Harmoni",
            shareloc = "",
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.onDeleteClick()
        assertTrue(viewModel.uiState.value.showDeleteDialog)

        viewModel.onDeleteDismiss()
        assertFalse(viewModel.uiState.value.showDeleteDialog)
        assertFalse(viewModel.uiState.value.isDeleted)

        // Verifikasi job masih ada di gateway
        assertNotNull(jobGateway.observeJob(id).first())
    }

    @Test
    fun `delete confirmation successfully deletes job and sets isDeleted`() = runBlocking {
        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            acara = "Resepsi",
            alamat = "Gedung Harmoni",
            shareloc = "",
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.onDeleteClick()
        assertTrue(viewModel.uiState.value.showDeleteDialog)

        viewModel.onDeleteConfirm()

        val state = viewModel.uiState.value
        assertFalse(state.showDeleteDialog)
        assertTrue(state.isDeleted)

        // Verifikasi job sudah dihapus dari gateway
        assertNull(jobGateway.observeJob(id).first())
    }

    @Test
    fun `delete failure keeps detail open and displays error message`() = runBlocking {
        // Set scenario error pada gateway terlebih dahulu
        jobGateway.setScenario(DemoScenario.DELETE_ERROR)

        val draft = JobDraft(
            namaPengantin1 = "Andi",
            namaPengantin2 = "Sari",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            acara = "Resepsi",
            alamat = "Gedung Harmoni",
            shareloc = "",
            sumberJob = "Vendor A",
            fee = 3_500_000L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.onDeleteClick()
        viewModel.onDeleteConfirm()

        val state = viewModel.uiState.value
        assertFalse(state.isDeleted)
        assertFalse(state.showDeleteDialog)
        assertEquals("Simulasi gagal hapus", state.userMessage)

        // Verifikasi job tetap ada
        assertNotNull(jobGateway.observeJob(id).first())
    }

    // ── FE-14 Payment Tests ────────────────────────────────────────────────────

    @Test
    fun `edit payment to valid amount updates fee, paid, and remaining`() = runBlocking {
        // Step 1: Fee Rp 2.000.000, dibayar Rp 500.000
        val draft = JobDraft(
            namaPengantin1 = "Riko",
            namaPengantin2 = "Dewi",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 180,
            acara = "Akad",
            alamat = "",
            shareloc = "",
            sumberJob = "Pribadi",
            fee = 2_000_000L,
            dibayar = 500_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertTrue(viewModel.uiState.value.isMarkPaidVisible)
        assertEquals("Rp 500.000", viewModel.uiState.value.jobDisplay?.dibayarFormatted)
        assertEquals("Rp 1.500.000", viewModel.uiState.value.jobDisplay?.sisaFormatted)

        // Buka dialog ubah pembayaran
        viewModel.onEditPaymentClick()
        assertTrue(viewModel.uiState.value.showPaymentDialog)
        assertEquals("500000", viewModel.uiState.value.paymentDraftText)

        // Ubah total ke Rp 800.000
        viewModel.onPaymentDraftChange("800000")
        assertNull(viewModel.uiState.value.paymentDraftErrorResId)

        // Konfirmasi simpan
        viewModel.onPaymentDialogConfirm()

        val state = viewModel.uiState.value
        assertFalse(state.showPaymentDialog)
        assertEquals(800_000L, state.job?.dibayar)
        assertEquals("Rp 800.000", state.jobDisplay?.dibayarFormatted)
        assertEquals("Rp 1.200.000", state.jobDisplay?.sisaFormatted)
        assertEquals("Kurang Rp 1.200.000", state.jobDisplay?.paymentChipText)
        assertTrue(state.isMarkPaidVisible)
    }

    @Test
    fun `edit payment exceeding fee shows inline error and keeps existing paid value`() = runBlocking {
        // Step 2: Masukkan nilai di atas fee; dialog tetap terbuka dengan error
        val draft = JobDraft(
            namaPengantin1 = "Riko",
            namaPengantin2 = "Dewi",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 180,
            acara = "Akad",
            alamat = "",
            shareloc = "",
            sumberJob = "Pribadi",
            fee = 2_000_000L,
            dibayar = 500_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        viewModel.onEditPaymentClick()
        viewModel.onPaymentDraftChange("2500000") // 2.5jt > 2jt fee

        val state = viewModel.uiState.value
        assertEquals(com.example.kontenin.R.string.validation_err_paid_exceeds_fee, state.paymentDraftErrorResId)

        // Menekan simpan tidak akan memutasi
        viewModel.onPaymentDialogConfirm()

        assertTrue(viewModel.uiState.value.showPaymentDialog)
        // Data di gateway dan state tetap Rp 500.000
        assertEquals(500_000L, jobGateway.observeJob(id).first()?.dibayar)
        assertEquals(500_000L, viewModel.uiState.value.job?.dibayar)
    }

    @Test
    fun `mark paid sets dibayar equal to fee and hides mark paid button and chip`() = runBlocking {
        // Step 3: Tandai Lunas; chip dan tombol tersebut hilang
        val draft = JobDraft(
            namaPengantin1 = "Riko",
            namaPengantin2 = "Dewi",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 180,
            acara = "Akad",
            alamat = "",
            shareloc = "",
            sumberJob = "Pribadi",
            fee = 2_000_000L,
            dibayar = 500_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertTrue(viewModel.uiState.value.isMarkPaidVisible)
        assertNotNull(viewModel.uiState.value.jobDisplay?.paymentChipText)

        viewModel.onMarkPaidClick()

        val state = viewModel.uiState.value
        assertEquals(2_000_000L, state.job?.dibayar)
        assertEquals("Rp 2.000.000", state.jobDisplay?.dibayarFormatted)
        assertEquals("Rp 0", state.jobDisplay?.sisaFormatted)
        assertTrue(state.jobDisplay?.isLunas == true)
        assertNull(state.jobDisplay?.paymentChipText)
        assertFalse(state.isMarkPaidVisible)
    }

    @Test
    fun `job with fee 0 does not show mark paid button and dismiss dialog preserves paid amount`() = runBlocking {
        // Step 4: Fee 0 tidak menampilkan Tandai Lunas. Batalkan dialog; total tetap.
        val draft = JobDraft(
            namaPengantin1 = "Doni",
            namaPengantin2 = "Lia",
            tanggal = 20743L,
            jamMulaiMenit = 480,
            durasiMenit = 120,
            acara = "Lamaran",
            alamat = "",
            shareloc = "",
            sumberJob = "Keluarga",
            fee = 0L,
            dibayar = 0L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        assertFalse(viewModel.uiState.value.isMarkPaidVisible)

        // Buka dialog lalu batalkan
        viewModel.onEditPaymentClick()
        assertTrue(viewModel.uiState.value.showPaymentDialog)
        viewModel.onPaymentDraftChange("0")
        viewModel.onPaymentDialogDismiss()

        assertFalse(viewModel.uiState.value.showPaymentDialog)
        assertEquals(0L, viewModel.uiState.value.job?.dibayar)
    }

    @Test
    fun `simulated update payment failure keeps old amount and supports retry`() = runBlocking {
        // Step 5: Simulasikan gagal update; angka lama tetap dan retry dapat dilakukan
        val draft = JobDraft(
            namaPengantin1 = "Riko",
            namaPengantin2 = "Dewi",
            tanggal = 20743L,
            jamMulaiMenit = 600,
            durasiMenit = 180,
            acara = "Akad",
            alamat = "",
            shareloc = "",
            sumberJob = "Pribadi",
            fee = 2_000_000L,
            dibayar = 500_000L,
            catatan = "",
        )
        val saveResult = jobGateway.saveJob(draft) as JobResult.Success
        val id = saveResult.job.id

        val viewModel = JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = id,
            dispatcher = Dispatchers.Unconfined,
        )

        // Set skenario SAVE_ERROR pada job gateway tanpa me-reset data
        jobGateway.setScenario(DemoScenario.SAVE_ERROR, resetData = false)

        viewModel.onEditPaymentClick()
        viewModel.onPaymentDraftChange("1000000")
        viewModel.onPaymentDialogConfirm()

        // State harus menampilkan pesan error dan angka lama tetap
        assertEquals("Simulasi gagal update pembayaran", viewModel.uiState.value.userMessage)
        assertEquals(500_000L, viewModel.uiState.value.job?.dibayar)

        // Reset skenario ke SUCCESS untuk retry
        jobGateway.setScenario(DemoScenario.SUCCESS, resetData = false)

        // Retry operasi simpan pembayaran
        viewModel.onPaymentDialogConfirm()

        assertEquals(1_000_000L, viewModel.uiState.value.job?.dibayar)
        assertEquals("Rp 1.000.000", viewModel.uiState.value.jobDisplay?.dibayarFormatted)
        assertFalse(viewModel.uiState.value.showPaymentDialog)
    }
}
