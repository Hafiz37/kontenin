package com.example.kontenin.presentation

import com.example.kontenin.contract.JobResult
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.demo.DemoScenario
import com.example.kontenin.model.JobDraft
import com.example.kontenin.presentation.recap.RecapPeriod
import com.example.kontenin.presentation.recap.RecapViewModel
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class RecapViewModelTest {

    // 29 Oktober 2026 12:00:00 WIB
    private val clock = FixedClock(Instant.parse("2026-10-29T05:00:00Z"))
    private lateinit var gateway: DemoJobGateway
    private lateinit var viewModel: RecapViewModel

    @Before
    fun setUp() {
        gateway = DemoJobGateway(clock = clock, initialScenario = DemoScenario.EMPTY_DATA)
        viewModel = RecapViewModel(
            jobGateway = gateway,
            clock = clock,
            dispatcher = Dispatchers.Unconfined,
        )
    }

    @Test
    fun `initial empty gateway shows zero summary and empty lists`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(0, state.summary.jobCount)
        assertEquals(0L, state.summary.totalFee)
        assertEquals(0L, state.summary.totalDibayar)
        assertEquals(0L, state.summary.totalSisa)
        assertTrue(state.sourceRecaps.isEmpty())
        assertTrue(state.periodJobs.isEmpty())
        assertTrue(state.receivables.isEmpty())
    }

    @Test
    fun `adding jobs reactively updates summary, sources, and receivables`() = runBlocking {
        val epochOct29 = LocalDate.of(2026, 10, 29).toEpochDay()
        val epochOct20 = LocalDate.of(2026, 10, 20).toEpochDay()

        // Seed A: 29 Okt, selesai 10.00 (sudah lewat dari 12.00 WIB)
        val saveA = gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Andi",
                namaPengantin2 = "Sari",
                tanggal = epochOct29,
                jamMulaiMenit = 480,
                durasiMenit = 120,
                acara = "Akad",
                alamat = "",
                shareloc = "",
                sumberJob = "Vendor X",
                fee = 1_000_000L,
                dibayar = 400_000L,
                catatan = "",
            )
        ) as JobResult.Success

        // Seed B: 29 Okt, 15.00 lunas
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Budi",
                namaPengantin2 = "Rina",
                tanggal = epochOct29,
                jamMulaiMenit = 900,
                durasiMenit = 180,
                acara = "Resepsi",
                alamat = "",
                shareloc = "",
                sumberJob = "Pribadi",
                fee = 2_000_000L,
                dibayar = 2_000_000L,
                catatan = "",
            )
        )

        // Seed C: 20 Okt belum lunas
        gateway.saveJob(
            JobDraft(
                namaPengantin1 = "Candra",
                namaPengantin2 = "Citra",
                tanggal = epochOct20,
                jamMulaiMenit = 600,
                durasiMenit = 240,
                acara = "Resepsi",
                alamat = "",
                shareloc = "",
                sumberJob = "Vendor Y",
                fee = 500_000L,
                dibayar = 0L,
                catatan = "",
            )
        )

        val stateToday = viewModel.uiState.value
        assertEquals(2, stateToday.summary.jobCount)
        assertEquals(3_000_000L, stateToday.summary.totalFee)
        assertEquals(2_400_000L, stateToday.summary.totalDibayar)
        assertEquals(600_000L, stateToday.summary.totalSisa)

        // Piutang: 2 job (C lalu A)
        assertEquals(2, stateToday.receivables.size)
        assertEquals(1_100_000L, stateToday.totalReceivablesSisa)

        // Pindah periode ke Bulan Ini
        viewModel.onPeriodSelected(RecapPeriod.THIS_MONTH)
        val stateMonth = viewModel.uiState.value
        assertEquals(RecapPeriod.THIS_MONTH, stateMonth.selectedPeriod)
        assertEquals(3, stateMonth.summary.jobCount)
        assertEquals(3_500_000L, stateMonth.summary.totalFee)
        assertEquals(1_100_000L, stateMonth.summary.totalSisa)

        // Step 4: Tandai A lunas di gateway -> rekap dan piutang langsung berubah
        gateway.updatePayment(saveA.job.id, 1_000_000L)

        val updatedState = viewModel.uiState.value
        assertEquals(3_000_000L, updatedState.summary.totalDibayar)
        assertEquals(500_000L, updatedState.summary.totalSisa)
        assertEquals(1, updatedState.receivables.size) // Hanya tersisa C
        assertEquals(500_000L, updatedState.totalReceivablesSisa)
    }
}
