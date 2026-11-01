package com.example.kontenin.demo

import com.example.kontenin.contract.ConflictCheckResult
import com.example.kontenin.contract.JobResult
import com.example.kontenin.model.JobDraft
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

class DemoVerificationTest {
    private lateinit var gateway: DemoJobGateway
    private val clock = FixedClock(Instant.parse("2026-10-29T05:00:00Z"))

    @Before fun reset() {
        gateway = DemoJobGateway(clock, DemoScenario.EMPTY_DATA)
    }

    private fun draft() = JobDraft(
        namaPengantin1 = "Andi", namaPengantin2 = "Sari",
        tanggal = clock.nowLocalDate().toEpochDay(), jamMulaiMenit = 1320,
        durasiMenit = 240, acara = "Resepsi", sumberJob = "Pribadi", fee = 1_000_000L,
    )

    @Test fun fixturesUseInjectedClockAndResetExactly() = runBlocking {
        gateway.setScenario(DemoScenario.SUCCESS)
        val before = gateway.observeAllJobs().first()
        assertEquals(clock.nowLocalDate().toEpochDay(), before.single { it.id == 2L }.tanggal)
        gateway.deleteJob(2L)
        gateway.resetToSeed()
        assertEquals(before, gateway.observeAllJobs().first())
        assertEquals(DemoFixtures.getLargeDatasetSeed(clock), DemoFixtures.getLargeDatasetSeed(clock))
    }

    @Test fun overlapAcrossMidnightIsReportedInBothDirections() = runBlocking {
        val night = (gateway.saveJob(draft()) as JobResult.Success).job
        val nextDay = night.tanggal + 1
        val overlap = gateway.checkConflict(nextDay, 60, 120, null)
        assertEquals(listOf(night), (overlap as ConflictCheckResult.HasConflict).conflictingJobs)
        gateway.resetToSeed()
        val morning = (gateway.saveJob(draft().copy(tanggal = nextDay, jamMulaiMenit = 60)) as JobResult.Success).job
        assertEquals(listOf(morning), (gateway.checkConflict(night.tanggal, 1320, 240, null)
            as ConflictCheckResult.HasConflict).conflictingJobs)
    }

    @Test fun adjacentJobsAndEditingSelfAreNotConflicts() = runBlocking {
        val night = (gateway.saveJob(draft()) as JobResult.Success).job
        assertEquals(ConflictCheckResult.NoConflict, gateway.checkConflict(night.tanggal + 1, 120, 60, null))
        assertEquals(ConflictCheckResult.NoConflict, gateway.checkConflict(night.tanggal, 1320, 240, night.id))
    }

    @Test fun failedSaveLeavesEntireCollectionUnchanged() = runBlocking {
        gateway.setScenario(DemoScenario.SAVE_ERROR)
        val before = gateway.observeAllJobs().first()
        assertTrue(gateway.saveJob(draft()) is JobResult.Error)
        assertEquals(before, gateway.observeAllJobs().first())
    }

    @Test fun pendingSaveDoesNotPublishPartialDataBeforeRelease() = runBlocking {
        gateway.setScenario(DemoScenario.PENDING)
        val before = gateway.observeAllJobs().first()
        val save = async(start = CoroutineStart.UNDISPATCHED) { gateway.saveJob(draft()) }
        try {
            assertFalse(save.isCompleted)
            assertEquals(before, gateway.observeAllJobs().first())
            gateway.releasePendingOperation()
            val result = withTimeout(2_000) { save.await() }
            assertTrue(result is JobResult.Success)
            assertEquals(before.size + 1, gateway.observeAllJobs().first().size)
        } finally {
            save.cancel()
        }
    }

}
