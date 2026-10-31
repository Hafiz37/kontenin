package com.example.kontenin.presentation.home

import com.example.kontenin.model.Job
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * HomeRules untuk Kontenin - FE-07.
 * 
 * Pure functions untuk kategori, sorting, dan filtering job di Beranda.
 * Semua logic murni tanpa efek samping, sehingga mudah ditest.
 * 
 * Keputusan Frontend yang Dicatat:
 * - Job dianggap selesai saat: now >= (startTime + duration)
 * - Job "Mendatang" = waktu selesai belum tercapai (termasuk sedang berlangsung)
 * - Job "Riwayat" = waktu selesai sudah tercapai
 * - Tie-breaker sorting: ID ascending (deterministik)
 */

private val WIB_ZONE = ZoneId.of("Asia/Jakarta")

/**
 * Menghitung waktu mulai job sebagai LocalDateTime WIB.
 */
fun Job.calculateStartDateTime(): LocalDateTime {
    val localDate = LocalDate.ofEpochDay(tanggal)
    val hours = jamMulaiMenit / 60
    val minutes = jamMulaiMenit % 60
    return localDate.atTime(hours, minutes)
}

/**
 * Menghitung waktu selesai job sebagai LocalDateTime WIB.
 */
fun Job.calculateEndDateTime(): LocalDateTime {
    return calculateStartDateTime().plusMinutes(durasiMenit.toLong())
}

/**
 * Mengecek apakah job adalah "Mendatang" (belum selesai).
 * 
 * Kriteria: waktu selesai job (start + duration) > now
 * 
 * Job yang sedang berlangsung dianggap "Mendatang".
 * 
 * @param now waktu sekarang dari AppClock
 * @return true jika job belum selesai
 */
fun Job.isUpcoming(now: Instant): Boolean {
    val endDateTime = calculateEndDateTime()
    val endInstant = endDateTime.atZone(WIB_ZONE).toInstant()
    return now < endInstant
}

/**
 * Mengecek apakah job adalah "Riwayat" (sudah selesai).
 * 
 * Kriteria: waktu selesai job (start + duration) <= now
 * 
 * @param now waktu sekarang dari AppClock
 * @return true jika job sudah selesai
 */
fun Job.isHistory(now: Instant): Boolean {
    return !isUpcoming(now)
}

/**
 * Mengecek apakah job belum lunas.
 * 
 * Kriteria: dibayar < fee
 * Fee = 0 dianggap lunas (sesuai FR-3).
 * 
 * @return true jika belum lunas
 */
fun Job.isUnpaid(): Boolean {
    return dibayar < fee && fee > 0
}

/**
 * Sort job Mendatang: waktu mulai terdekat dulu (ASC), ID sebagai tie-breaker.
 * 
 * @return list job terurut
 */
fun List<Job>.sortForUpcoming(): List<Job> {
    return sortedWith(compareBy<Job> { it.calculateStartDateTime() }.thenBy { it.id })
}

/**
 * Sort job Riwayat: waktu mulai terbaru dulu (DESC), ID sebagai tie-breaker.
 * 
 * @return list job terurut
 */
fun List<Job>.sortForHistory(): List<Job> {
    return sortedWith(compareByDescending<Job> { it.calculateStartDateTime() }.thenBy { it.id })
}

/**
 * Filter job yang belum lunas saja.
 * 
 * @return list job belum lunas
 */
fun List<Job>.filterUnpaid(): List<Job> {
    return filter { it.isUnpaid() }
}

/**
 * Kategorisasi dan sorting job untuk Beranda.
 * 
 * @param jobs list semua job
 * @param now waktu sekarang
 * @return Pair(upcomingJobs, historyJobs) yang sudah terurut
 */
fun categorizeAndSortJobs(jobs: List<Job>, now: Instant): Pair<List<Job>, List<Job>> {
    val upcoming = jobs.filter { it.isUpcoming(now) }.sortForUpcoming()
    val history = jobs.filter { it.isHistory(now) }.sortForHistory()
    return Pair(upcoming, history)
}
