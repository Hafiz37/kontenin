package com.example.kontenin.presentation

import com.example.kontenin.model.Job
import com.example.kontenin.presentation.home.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Unit tests untuk HomeRules - FE-07.
 * 
 * Menguji logic kategori, sorting, dan filtering job di Beranda.
 */
class HomeRulesTest {
    
    private val wibZone = ZoneId.of("Asia/Jakarta")
    
    private fun createJob(
        id: Long,
        tanggal: Long, // epoch day
        jamMulaiMenit: Int,
        durasiMenit: Int,
        fee: Long = 1000000,
        dibayar: Long = 0
    ): Job {
        return Job(
            id = id,
            namaPengantin1 = "Test",
            namaPengantin2 = "User",
            tanggal = tanggal,
            jamMulaiMenit = jamMulaiMenit,
            durasiMenit = durasiMenit,
            acara = "Resepsi",
            alamat = "",
            shareloc = "",
            sumberJob = "Pribadi",
            fee = fee,
            dibayar = dibayar,
            catatan = "",
            createdAt = 0L,
            updatedAt = 0L
        )
    }
    
    private fun instantFromWib(dateTime: LocalDateTime): Instant {
        return dateTime.atZone(wibZone).toInstant()
    }
    
    @Test
    fun `job dengan waktu selesai belum lewat adalah Mendatang`() {
        // Job: 31 Okt 2026, 10:00-14:00 (selesai 14:00)
        val job = createJob(
            id = 1,
            tanggal = LocalDate.of(2026, 10, 31).toEpochDay(),
            jamMulaiMenit = 10 * 60, // 10:00
            durasiMenit = 4 * 60 // 4 jam
        )
        
        // Now: 31 Okt 2026, 12:00 (job sedang berlangsung)
        val now = instantFromWib(LocalDateTime.of(2026, 10, 31, 12, 0))
        
        assertTrue("Job sedang berlangsung harus Mendatang", job.isUpcoming(now))
        assertFalse("Job sedang berlangsung bukan Riwayat", job.isHistory(now))
    }
    
    @Test
    fun `job dengan waktu selesai tepat now adalah Riwayat`() {
        // Job: 31 Okt 2026, 10:00-14:00 (selesai 14:00)
        val job = createJob(
            id = 1,
            tanggal = LocalDate.of(2026, 10, 31).toEpochDay(),
            jamMulaiMenit = 10 * 60,
            durasiMenit = 4 * 60
        )
        
        // Now: tepat 31 Okt 2026, 14:00 (job baru selesai)
        val now = instantFromWib(LocalDateTime.of(2026, 10, 31, 14, 0))
        
        assertFalse("Job tepat selesai bukan Mendatang", job.isUpcoming(now))
        assertTrue("Job tepat selesai adalah Riwayat", job.isHistory(now))
    }
    
    @Test
    fun `job masa depan 1 jam lagi adalah Mendatang`() {
        // Job: 31 Okt 2026, 15:00-17:00
        val job = createJob(
            id = 1,
            tanggal = LocalDate.of(2026, 10, 31).toEpochDay(),
            jamMulaiMenit = 15 * 60,
            durasiMenit = 2 * 60
        )
        
        // Now: 31 Okt 2026, 14:00 (1 jam sebelum mulai)
        val now = instantFromWib(LocalDateTime.of(2026, 10, 31, 14, 0))
        
        assertTrue("Job masa depan adalah Mendatang", job.isUpcoming(now))
    }
    
    @Test
    fun `job masa lalu adalah Riwayat`() {
        // Job: 29 Okt 2026, 08:00-10:00 (selesai 10:00)
        val job = createJob(
            id = 1,
            tanggal = LocalDate.of(2026, 10, 29).toEpochDay(),
            jamMulaiMenit = 8 * 60,
            durasiMenit = 2 * 60
        )
        
        // Now: 31 Okt 2026, 12:00
        val now = instantFromWib(LocalDateTime.of(2026, 10, 31, 12, 0))
        
        assertTrue("Job masa lalu adalah Riwayat", job.isHistory(now))
        assertFalse("Job masa lalu bukan Mendatang", job.isUpcoming(now))
    }
    
    @Test
    fun `sortUpcoming urut terdekat dulu dengan ID sebagai tiebreaker`() {
        val epochDay31Okt = LocalDate.of(2026, 10, 31).toEpochDay()
        val epochDay1Nov = LocalDate.of(2026, 11, 1).toEpochDay()
        
        val job1 = createJob(id = 3, tanggal = epochDay1Nov, jamMulaiMenit = 9 * 60, durasiMenit = 120)
        val job2 = createJob(id = 1, tanggal = epochDay31Okt, jamMulaiMenit = 15 * 60, durasiMenit = 120)
        val job3 = createJob(id = 2, tanggal = epochDay31Okt, jamMulaiMenit = 15 * 60, durasiMenit = 120) // Same time as job2
        
        val jobs = listOf(job1, job2, job3)
        val sorted = jobs.sortForUpcoming()
        
        // Expected: job2 (31 Okt 15:00, id=1), job3 (31 Okt 15:00, id=2), job1 (1 Nov 09:00, id=3)
        assertEquals(3, sorted.size)
        assertEquals(1L, sorted[0].id) // job2: terdekat, ID kecil
        assertEquals(2L, sorted[1].id) // job3: terdekat, ID lebih besar
        assertEquals(3L, sorted[2].id) // job1: paling jauh
    }
    
    @Test
    fun `sortHistory urut terbaru dulu dengan ID sebagai tiebreaker`() {
        val epochDay29Okt = LocalDate.of(2026, 10, 29).toEpochDay()
        val epochDay30Okt = LocalDate.of(2026, 10, 30).toEpochDay()
        
        val job1 = createJob(id = 3, tanggal = epochDay29Okt, jamMulaiMenit = 8 * 60, durasiMenit = 120)
        val job2 = createJob(id = 1, tanggal = epochDay30Okt, jamMulaiMenit = 10 * 60, durasiMenit = 120)
        val job3 = createJob(id = 2, tanggal = epochDay30Okt, jamMulaiMenit = 10 * 60, durasiMenit = 120) // Same time as job2
        
        val jobs = listOf(job1, job2, job3)
        val sorted = jobs.sortForHistory()
        
        // Expected: job2 (30 Okt, id=1), job3 (30 Okt, id=2), job1 (29 Okt, id=3)
        assertEquals(3, sorted.size)
        assertEquals(1L, sorted[0].id) // job2: terbaru, ID kecil
        assertEquals(2L, sorted[1].id) // job3: terbaru, ID lebih besar
        assertEquals(3L, sorted[2].id) // job1: paling lama
    }
    
    @Test
    fun `filterUnpaid hanya ambil dibayar kurang dari fee`() {
        val epochDay = LocalDate.of(2026, 10, 31).toEpochDay()
        
        val jobUnpaid = createJob(id = 1, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 0)
        val jobPartial = createJob(id = 2, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 500000)
        val jobPaid = createJob(id = 3, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 1000000)
        val jobOverpaid = createJob(id = 4, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 1500000)
        val jobNoFee = createJob(id = 5, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 0, dibayar = 0)
        
        val jobs = listOf(jobUnpaid, jobPartial, jobPaid, jobOverpaid, jobNoFee)
        val filtered = jobs.filterUnpaid()
        
        assertEquals(2, filtered.size)
        assertTrue("Belum bayar harus masuk filter", filtered.any { it.id == 1L })
        assertTrue("Bayar sebagian harus masuk filter", filtered.any { it.id == 2L })
        assertFalse("Lunas tidak masuk filter", filtered.any { it.id == 3L })
        assertFalse("Overpaid tidak masuk filter", filtered.any { it.id == 4L })
        assertFalse("Fee 0 tidak masuk filter unpaid", filtered.any { it.id == 5L })
    }
    
    @Test
    fun `categorizeAndSortJobs memisahkan dan mengurutkan dengan benar`() {
        val epochDay29 = LocalDate.of(2026, 10, 29).toEpochDay()
        val epochDay31 = LocalDate.of(2026, 10, 31).toEpochDay()
        val epochDay1Nov = LocalDate.of(2026, 11, 1).toEpochDay()
        
        // Job A: 29 Okt 08:00-10:00 (selesai 10:00) - masa lalu
        val jobA = createJob(id = 1, tanggal = epochDay29, jamMulaiMenit = 8 * 60, durasiMenit = 2 * 60)
        
        // Job B: 31 Okt 10:00-14:00 (selesai 14:00) - sedang berlangsung
        val jobB = createJob(id = 2, tanggal = epochDay31, jamMulaiMenit = 10 * 60, durasiMenit = 4 * 60)
        
        // Job C: 1 Nov 09:00-12:00 - masa depan
        val jobC = createJob(id = 3, tanggal = epochDay1Nov, jamMulaiMenit = 9 * 60, durasiMenit = 3 * 60)
        
        val jobs = listOf(jobC, jobA, jobB) // Acak
        
        // Now: 31 Okt 2026, 12:00
        val now = instantFromWib(LocalDateTime.of(2026, 10, 31, 12, 0))
        
        val (upcoming, history) = categorizeAndSortJobs(jobs, now)
        
        // Mendatang: B (sedang berlangsung, terdekat), C (masa depan)
        assertEquals(2, upcoming.size)
        assertEquals(2L, upcoming[0].id) // Job B: 31 Okt 10:00
        assertEquals(3L, upcoming[1].id) // Job C: 1 Nov 09:00
        
        // Riwayat: A (masa lalu)
        assertEquals(1, history.size)
        assertEquals(1L, history[0].id) // Job A: 29 Okt 08:00
    }
    
    @Test
    fun `isUnpaid mendeteksi status pembayaran dengan benar`() {
        val epochDay = LocalDate.of(2026, 10, 31).toEpochDay()
        
        val unpaid = createJob(id = 1, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 0)
        val partial = createJob(id = 2, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 500000)
        val paid = createJob(id = 3, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 1000000, dibayar = 1000000)
        val noFee = createJob(id = 4, tanggal = epochDay, jamMulaiMenit = 600, durasiMenit = 120, fee = 0, dibayar = 0)
        
        assertTrue("Belum bayar adalah unpaid", unpaid.isUnpaid())
        assertTrue("Bayar sebagian adalah unpaid", partial.isUnpaid())
        assertFalse("Lunas bukan unpaid", paid.isUnpaid())
        assertFalse("Fee 0 bukan unpaid", noFee.isUnpaid())
    }
}
