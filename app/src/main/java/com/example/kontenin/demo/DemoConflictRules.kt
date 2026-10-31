package com.example.kontenin.demo

import com.example.kontenin.model.Job

/**
 * Aturan perhitungan jadwal bertabrakan (bentrok) untuk demo frontend (FE-12).
 *
 * Acuan: PRD FR-1.8; EC-4–6, EC-25; DESIGN D-5, D-7.
 * - Menggunakan interval menit absolut dari epoch day (tanggal * 1440L + jamMulaiMenit).
 * - Kandidat tanggal: rentang minus satu hingga plus satu hari (tanggal - 1 .. tanggal + 1).
 * - Dua interval [mulaiA, selesaiA) dan [mulaiB, selesaiB) overlap jika:
 *   mulaiA < selesaiB && mulaiB < selesaiA.
 * - Abaikan ID sendiri saat ubah (excludeId) (EC-6).
 * - Interval berurutan tanpa jeda (mulaiB == selesaiA) bukan bentrok (EC-5).
 */
object DemoConflictRules {

    /**
     * Konversi tanggal epoch day dan jam menit ke menit absolut dari epoch 1970-01-01.
     */
    fun toAbsoluteMinutes(tanggalEpochDay: Long, jamMulaiMenit: Int): Long {
        return tanggalEpochDay * 1440L + jamMulaiMenit
    }

    /**
     * Cek apakah dua interval waktu absolut [startA, endA) dan [startB, endB) tumpang tindih.
     * Interval berurutan tepat (startA == endB atau startB == endA) BUKAN tumpang tindih.
     */
    fun isOverlapping(
        startA: Long,
        endA: Long,
        startB: Long,
        endB: Long
    ): Boolean {
        return startA < endB && startB < endA
    }

    /**
     * Mencari seluruh job dalam [allJobs] yang bertabrakan dengan jadwal yang diberikan.
     *
     * @param tanggal Epoch day dari tanggal acara
     * @param jamMulaiMenit Menit mulai dari tengah malam (0..1439)
     * @param durasiMenit Durasi acara dalam menit (30..1440)
     * @param allJobs Kumpulan job yang ada
     * @param excludeId ID job yang diabaikan (saat mode ubah)
     * @return Daftar job yang jadwalnya tumpang tindih
     */
    fun findConflicts(
        tanggal: Long,
        jamMulaiMenit: Int,
        durasiMenit: Int,
        allJobs: Collection<Job>,
        excludeId: Long? = null
    ): List<Job> {
        val startA = toAbsoluteMinutes(tanggal, jamMulaiMenit)
        val endA = startA + durasiMenit

        return allJobs.filter { job ->
            if (excludeId != null && job.id == excludeId) return@filter false
            // Kandidat dibatasi tanggal ±1 hari (EC-4)
            if (job.tanggal !in (tanggal - 1)..(tanggal + 1)) return@filter false

            val startB = toAbsoluteMinutes(job.tanggal, job.jamMulaiMenit)
            val endB = startB + job.durasiMenit

            isOverlapping(startA, endA, startB, endB)
        }
    }
}
