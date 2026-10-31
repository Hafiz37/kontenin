package com.example.kontenin.demo

import com.example.kontenin.model.Job
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test untuk DemoConflictRules (FE-12).
 *
 * Menguji:
 * - Interval berurutan tepat tanpa jeda (bukan bentrok - EC-5)
 * - Tumpang tindih menit (bentrok - testing step 1)
 * - Lintas hari / tengah malam (EC-4, testing step 2)
 * - Durasi 24 jam
 * - Pengecualian ID sendiri saat mode ubah (EC-6, testing step 3)
 * - Deteksi beberapa konflik sekaligus (testing step 5)
 * - Batasan kandidat tanggal ±1 hari
 */
class DemoConflictRulesTest {

    private fun dummyJob(
        id: Long,
        tanggal: Long,
        jamMulaiMenit: Int,
        durasiMenit: Int,
        nama: String = "Client $id",
        acara: String = "Acara $id"
    ): Job {
        return Job(
            id = id,
            namaPengantin1 = nama,
            namaPengantin2 = "Pasangan",
            tanggal = tanggal,
            jamMulaiMenit = jamMulaiMenit,
            durasiMenit = durasiMenit,
            acara = acara,
            alamat = "Alamat",
            shareloc = "",
            sumberJob = "Sumber",
            fee = 1000000L,
            dibayar = 0L,
            catatan = "",
            createdAt = 1000L,
            updatedAt = 1000L
        )
    }

    // ── 1. Uji Interval Berurutan vs Tumpang Tindih (Testing Step 1) ──────────

    @Test
    fun `job 10_00-14_00 bertemu 14_00-16_00 pada hari yang sama bukan bentrok`() {
        val tanggal = 20000L
        val jobExisting = dummyJob(id = 1L, tanggal = tanggal, jamMulaiMenit = 600, durasiMenit = 240) // 10.00 - 14.00

        // Job baru mulai 14.00 (840) durasi 120 (14.00 - 16.00)
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 840,
            durasiMenit = 120,
            allJobs = listOf(jobExisting)
        )

        assertTrue(conflicts.isEmpty())
    }

    @Test
    fun `job 10_00-14_00 bertemu 13_59-15_00 pada hari yang sama terdeteksi bentrok`() {
        val tanggal = 20000L
        val jobExisting = dummyJob(id = 1L, tanggal = tanggal, jamMulaiMenit = 600, durasiMenit = 240) // 10.00 - 14.00

        // Job baru mulai 13.59 (839) durasi 60 (13.59 - 14.59)
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 839,
            durasiMenit = 60,
            allJobs = listOf(jobExisting)
        )

        assertEquals(1, conflicts.size)
        assertEquals(1L, conflicts[0].id)
    }

    // ── 2. Uji Lintas Tengah Malam (EC-4, Testing Step 2) ───────────────────

    @Test
    fun `job 22_00-02_00 bertemu job hari berikut pukul 01_00 terdeteksi bentrok`() {
        val hari1 = 20000L
        val hari2 = 20001L

        // Job 1 di hari1: 22.00 (1320), durasi 4 jam (240 menit) -> selesai di hari2 pukul 02.00
        val jobLintasHari = dummyJob(id = 1L, tanggal = hari1, jamMulaiMenit = 1320, durasiMenit = 240)

        // Job 2 di hari2: 01.00 (60), durasi 2 jam (120 menit)
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = hari2,
            jamMulaiMenit = 60,
            durasiMenit = 120,
            allJobs = listOf(jobLintasHari)
        )

        assertEquals(1, conflicts.size)
        assertEquals(1L, conflicts[0].id)
    }

    @Test
    fun `job 22_00-02_00 bertemu job hari berikut pukul 02_00 pas selesai bukan bentrok`() {
        val hari1 = 20000L
        val hari2 = 20001L

        // Job 1 di hari1: 22.00 (1320), durasi 240 menit -> selesai pukul 02.00 hari2
        val jobLintasHari = dummyJob(id = 1L, tanggal = hari1, jamMulaiMenit = 1320, durasiMenit = 240)

        // Job 2 di hari2 mulai tepat pukul 02.00 (120)
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = hari2,
            jamMulaiMenit = 120,
            durasiMenit = 60,
            allJobs = listOf(jobLintasHari)
        )

        assertTrue(conflicts.isEmpty())
    }

    // ── 3. Uji Durasi Maksimal 24 Jam ────────────────────────────────────────

    @Test
    fun `job 24 jam pada hari D tumpang tindih dengan job pada hari D+1`() {
        val hariD = 20000L
        val hariDPlus1 = 20001L

        // Job 24 jam: 08.00 hari D s.d. 08.00 hari D+1
        val job24Jam = dummyJob(id = 10L, tanggal = hariD, jamMulaiMenit = 480, durasiMenit = 1440)

        // Job baru hari D+1 jam 07.00 - 09.00
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = hariDPlus1,
            jamMulaiMenit = 420,
            durasiMenit = 120,
            allJobs = listOf(job24Jam)
        )

        assertEquals(1, conflicts.size)
        assertEquals(10L, conflicts[0].id)
    }

    // ── 4. Uji Mode Ubah - Pengecualian ID Sendiri (EC-6, Testing Step 3) ────

    @Test
    fun `ubah job dengan jadwal sama tidak bentrok dengan dirinya sendiri`() {
        val tanggal = 20000L
        val myJob = dummyJob(id = 5L, tanggal = tanggal, jamMulaiMenit = 600, durasiMenit = 120)

        // Saat ubah job 5, excludeId = 5
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 600,
            durasiMenit = 120,
            allJobs = listOf(myJob),
            excludeId = 5L
        )

        assertTrue(conflicts.isEmpty())
    }

    @Test
    fun `ubah job yang menabrak job orang lain tetap terdeteksi`() {
        val tanggal = 20000L
        val myJob = dummyJob(id = 5L, tanggal = tanggal, jamMulaiMenit = 600, durasiMenit = 120)
        val otherJob = dummyJob(id = 6L, tanggal = tanggal, jamMulaiMenit = 660, durasiMenit = 120)

        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 600,
            durasiMenit = 120,
            allJobs = listOf(myJob, otherJob),
            excludeId = 5L
        )

        assertEquals(1, conflicts.size)
        assertEquals(6L, conflicts[0].id)
    }

    // ── 5. Uji Beberapa Konflik Sekaligus (Testing Step 5) ───────────────────

    @Test
    fun `jadwal yang menabrak beberapa job sekaligus mengembalikan seluruh konflik`() {
        val tanggal = 20000L
        val job1 = dummyJob(id = 1L, tanggal = tanggal, jamMulaiMenit = 600, durasiMenit = 120, nama = "Andi & Sari")
        val job2 = dummyJob(id = 2L, tanggal = tanggal, jamMulaiMenit = 660, durasiMenit = 120, nama = "Budi & Citra")

        // Job baru: 09.00 - 13.00 (540 s.d. 780), menabrak kedua job
        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 540,
            durasiMenit = 240,
            allJobs = listOf(job1, job2)
        )

        assertEquals(2, conflicts.size)
        assertEquals(listOf(1L, 2L), conflicts.map { it.id })
    }

    // ── 6. Uji Kandidat Tanggal Di Luar Rentang ±1 Hari ──────────────────────

    @Test
    fun `job pada tanggal minus 2 hari atau plus 2 hari diabaikan dari kandidat`() {
        val tanggal = 20000L
        val jobJauh = dummyJob(id = 99L, tanggal = tanggal - 2, jamMulaiMenit = 600, durasiMenit = 120)

        val conflicts = DemoConflictRules.findConflicts(
            tanggal = tanggal,
            jamMulaiMenit = 600,
            durasiMenit = 120,
            allJobs = listOf(jobJauh)
        )

        assertTrue(conflicts.isEmpty())
    }
}
