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
}
