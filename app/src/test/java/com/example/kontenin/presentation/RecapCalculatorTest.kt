package com.example.kontenin.presentation

import com.example.kontenin.model.Job
import com.example.kontenin.presentation.recap.RecapCalculator
import com.example.kontenin.presentation.recap.RecapPeriod
import com.example.kontenin.util.FixedClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class RecapCalculatorTest {

    // 29 Oktober 2026 12:00:00 WIB = 2026-10-29T05:00:00Z
    private val clockOct29Noon = FixedClock(Instant.parse("2026-10-29T05:00:00Z"))

    private fun createJob(
        id: Long,
        tanggal: Long,
        jamMulaiMenit: Int,
        durasiMenit: Int,
        fee: Long,
        dibayar: Long,
        sumberJob: String = "Vendor A",
        nama1: String = "Klien",
        nama2: String = "Pasangan",
    ): Job {
        return Job(
            id = id,
            namaPengantin1 = nama1,
            namaPengantin2 = nama2,
            tanggal = tanggal,
            jamMulaiMenit = jamMulaiMenit,
            durasiMenit = durasiMenit,
            acara = "Resepsi",
            alamat = "Gedung",
            shareloc = "",
            sumberJob = sumberJob,
            fee = fee,
            dibayar = dibayar,
            catatan = "",
            createdAt = 1000L,
            updatedAt = 1000L,
        )
    }

    @Test
    fun `testing step 1 and 2 - summary today and receivables list`() {
        val epochOct29 = LocalDate.of(2026, 10, 29).toEpochDay()
        val epochOct20 = LocalDate.of(2026, 10, 20).toEpochDay()

        // Job A: tanggal 29 pukul 08.00 durasi 2 jam (selesai 10.00), fee 1jt dibayar 400rb (sisa 600rb)
        val jobA = createJob(
            id = 1L,
            tanggal = epochOct29,
            jamMulaiMenit = 480, // 08.00
            durasiMenit = 120,   // 2 jam -> selesai 10.00
            fee = 1_000_000L,
            dibayar = 400_000L,
            sumberJob = "Vendor X",
            nama1 = "Andi",
            nama2 = "Sari",
        )

        // Job B: tanggal 29 pukul 15.00 fee 2jt lunas
        val jobB = createJob(
            id = 2L,
            tanggal = epochOct29,
            jamMulaiMenit = 900, // 15.00
            durasiMenit = 180,
            fee = 2_000_000L,
            dibayar = 2_000_000L,
            sumberJob = "Pribadi",
            nama1 = "Budi",
            nama2 = "Rina",
        )

        // Job C: tanggal 20 fee 500rb belum dibayar
        val jobC = createJob(
            id = 3L,
            tanggal = epochOct20,
            jamMulaiMenit = 600,
            durasiMenit = 240,
            fee = 500_000L,
            dibayar = 0L,
            sumberJob = "Vendor Y",
            nama1 = "Candra",
            nama2 = "Citra",
        )

        val allJobs = listOf(jobA, jobB, jobC)

        // 1. Hari Ini menghasilkan 2 job, fee 3.000.000, dibayar 2.400.000, sisa 600.000
        val todayJobs = RecapCalculator.filterJobsByPeriod(allJobs, RecapPeriod.TODAY, clockOct29Noon)
        assertEquals(2, todayJobs.size)
        assertTrue(todayJobs.contains(jobA))
        assertTrue(todayJobs.contains(jobB))

        val summaryToday = RecapCalculator.calculateSummary(todayJobs)
        assertEquals(2, summaryToday.jobCount)
        assertEquals(3_000_000L, summaryToday.totalFee)
        assertEquals(2_400_000L, summaryToday.totalDibayar)
        assertEquals(600_000L, summaryToday.totalSisa)

        // 2. Piutang menghasilkan C lalu A, total 1.100.000
        val receivables = RecapCalculator.filterReceivables(allJobs, clockOct29Noon)
        assertEquals(2, receivables.size)
        assertEquals(jobC.id, receivables[0].id) // C (tanggal 20)
        assertEquals(jobA.id, receivables[1].id) // A (tanggal 29)

        val receivablesSummary = RecapCalculator.calculateSummary(receivables)
        assertEquals(1_100_000L, receivablesSummary.totalSisa)
    }

    @Test
    fun `switch period changes summary while receivables remain constant`() {
        val epochOct29 = LocalDate.of(2026, 10, 29).toEpochDay()
        val epochOct20 = LocalDate.of(2026, 10, 20).toEpochDay()

        val jobA = createJob(1L, epochOct29, 480, 120, 1_000_000L, 400_000L)
        val jobB = createJob(2L, epochOct29, 900, 180, 2_000_000L, 2_000_000L)
        val jobC = createJob(3L, epochOct20, 600, 240, 500_000L, 0L)
        val allJobs = listOf(jobA, jobB, jobC)

        // Piutang selalu C lalu A
        val receivables = RecapCalculator.filterReceivables(allJobs, clockOct29Noon)
        assertEquals(listOf(jobC.id, jobA.id), receivables.map { it.id })

        // Hari Ini: hanya A & B
        val todaySummary = RecapCalculator.calculateSummary(
            RecapCalculator.filterJobsByPeriod(allJobs, RecapPeriod.TODAY, clockOct29Noon)
        )
        assertEquals(2, todaySummary.jobCount)

        // Minggu Ini (26 Okt - 1 Nov 2026): hanya A & B
        val weekSummary = RecapCalculator.calculateSummary(
            RecapCalculator.filterJobsByPeriod(allJobs, RecapPeriod.THIS_WEEK, clockOct29Noon)
        )
        assertEquals(2, weekSummary.jobCount)

        // Bulan Ini (1 Okt - 31 Okt 2026): memuat A, B, dan C (3 job)
        val monthSummary = RecapCalculator.calculateSummary(
            RecapCalculator.filterJobsByPeriod(allJobs, RecapPeriod.THIS_MONTH, clockOct29Noon)
        )
        assertEquals(3, monthSummary.jobCount)
        assertEquals(3_500_000L, monthSummary.totalFee)
        assertEquals(2_400_000L, monthSummary.totalDibayar)
        assertEquals(1_100_000L, monthSummary.totalSisa)
    }

    @Test
    fun `week crossing month boundary correctly filters Monday to Sunday`() {
        // Misal hari Rabu, 1 Juli 2026: minggu dimulai Senin 29 Juni 2026 s.d. Minggu 5 Juli 2026
        val clockJuly1 = FixedClock(Instant.parse("2026-07-01T05:00:00Z"))
        val epochJune29 = LocalDate.of(2026, 6, 29).toEpochDay() // Senin (bulan lalu)
        val epochJuly1 = LocalDate.of(2026, 7, 1).toEpochDay()   // Rabu
        val epochJuly5 = LocalDate.of(2026, 7, 5).toEpochDay()   // Minggu
        val epochJuly6 = LocalDate.of(2026, 7, 6).toEpochDay()   // Senin minggu depan

        val jobJune29 = createJob(1L, epochJune29, 600, 120, 1_000_000L, 0L)
        val jobJuly1 = createJob(2L, epochJuly1, 600, 120, 1_000_000L, 0L)
        val jobJuly5 = createJob(3L, epochJuly5, 600, 120, 1_000_000L, 0L)
        val jobJuly6 = createJob(4L, epochJuly6, 600, 120, 1_000_000L, 0L)

        val weekJobs = RecapCalculator.filterJobsByPeriod(
            listOf(jobJune29, jobJuly1, jobJuly5, jobJuly6),
            RecapPeriod.THIS_WEEK,
            clockJuly1
        )

        assertEquals(3, weekJobs.size)
        assertTrue(weekJobs.any { it.id == 1L })
        assertTrue(weekJobs.any { it.id == 2L })
        assertTrue(weekJobs.any { it.id == 3L })
        assertFalse(weekJobs.any { it.id == 4L })
    }

    @Test
    fun `calculateSourceRecaps groups by source with accurate totals`() {
        val epoch = LocalDate.of(2026, 10, 29).toEpochDay()
        val job1 = createJob(1L, epoch, 600, 120, 3_000_000L, 1_500_000L, sumberJob = "Vendor A")
        val job2 = createJob(2L, epoch, 600, 120, 3_000_000L, 3_000_000L, sumberJob = "Vendor A")
        val job3 = createJob(3L, epoch, 600, 120, 3_000_000L, 3_000_000L, sumberJob = "Vendor A")
        val job4 = createJob(4L, epoch, 600, 120, 3_000_000L, 3_000_000L, sumberJob = "Pribadi")

        val sourceRecaps = RecapCalculator.calculateSourceRecaps(listOf(job1, job2, job3, job4))

        assertEquals(2, sourceRecaps.size)

        // Vendor A: 3 job, total fee 9jt, sisa 1.5jt
        assertEquals("Vendor A", sourceRecaps[0].sourceName)
        assertEquals(3, sourceRecaps[0].jobCount)
        assertEquals(9_000_000L, sourceRecaps[0].totalFee)
        assertEquals(1_500_000L, sourceRecaps[0].totalSisa)

        // Pribadi: 1 job, total fee 3jt, sisa 0
        assertEquals("Pribadi", sourceRecaps[1].sourceName)
        assertEquals(1, sourceRecaps[1].jobCount)
        assertEquals(3_000_000L, sourceRecaps[1].totalFee)
        assertEquals(0L, sourceRecaps[1].totalSisa)
    }

    @Test
    fun `job with fee zero or not yet ended is not a receivable`() {
        val epochOct29 = LocalDate.of(2026, 10, 29).toEpochDay()

        // 1. Fee 0 tidak masuk piutang meskipun sudah lewat
        val jobZeroFee = createJob(1L, epochOct29, 480, 120, fee = 0L, dibayar = 0L)

        // 2. Acara belum selesai pada 12.00 WIB (mulai 15.00) tidak masuk piutang
        val jobFuture = createJob(2L, epochOct29, 900, 180, fee = 2_000_000L, dibayar = 0L)

        val receivables = RecapCalculator.filterReceivables(listOf(jobZeroFee, jobFuture), clockOct29Noon)
        assertTrue(receivables.isEmpty())
    }

    @Test
    fun `empty period returns zero summary and empty sources`() {
        val summary = RecapCalculator.calculateSummary(emptyList())
        assertEquals(0, summary.jobCount)
        assertEquals(0L, summary.totalFee)
        assertEquals(0L, summary.totalDibayar)
        assertEquals(0L, summary.totalSisa)

        val sources = RecapCalculator.calculateSourceRecaps(emptyList())
        assertTrue(sources.isEmpty())
    }
}
