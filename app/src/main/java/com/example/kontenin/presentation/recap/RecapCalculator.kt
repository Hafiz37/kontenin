package com.example.kontenin.presentation.recap

import com.example.kontenin.model.Job
import com.example.kontenin.util.AppClock
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

/**
 * Pilihan periode untuk Rekap (FE-15 / PRD FR-5.1).
 */
enum class RecapPeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

/**
 * Data ringkasan pendapatan untuk periode tertentu.
 */
data class RecapSummary(
    val jobCount: Int = 0,
    val totalFee: Long = 0L,
    val totalDibayar: Long = 0L,
    val totalSisa: Long = 0L,
)

/**
 * Data ringkasan per sumber job.
 */
data class SourceRecap(
    val sourceName: String,
    val jobCount: Int,
    val totalFee: Long,
    val totalSisa: Long,
)

/**
 * Perhitungan agregasi murni untuk Rekap, Per Sumber, dan Piutang (FE-15).
 *
 * Mengikuti aturan PRD FR-5, OQ-5, OQ-9:
 * - Periode dihitung dari tanggal mulai acara dalam zona Asia/Jakarta
 * - Minggu mulai Senin sampai Minggu
 * - Piutang: job yang waktu selesainya telah tercapai dan belum lunas, diurutkan tanggal terlama
 * - Fee nol bukan piutang
 */
object RecapCalculator {

    /**
     * Filter job berdasarkan periode yang dipilih.
     */
    fun filterJobsByPeriod(
        jobs: List<Job>,
        period: RecapPeriod,
        clock: AppClock,
    ): List<Job> {
        val today = clock.nowLocalDate()
        val todayEpoch = today.toEpochDay()

        return when (period) {
            RecapPeriod.TODAY -> {
                jobs.filter { it.tanggal == todayEpoch }
            }
            RecapPeriod.THIS_WEEK -> {
                val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()
                val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toEpochDay()
                jobs.filter { it.tanggal in monday..sunday }
            }
            RecapPeriod.THIS_MONTH -> {
                val firstDay = today.withDayOfMonth(1).toEpochDay()
                val lastDay = today.withDayOfMonth(today.lengthOfMonth()).toEpochDay()
                jobs.filter { it.tanggal in firstDay..lastDay }
            }
        }.sortedWith(compareByDescending<Job> { it.tanggal }.thenByDescending { it.jamMulaiMenit })
    }

    /**
     * Hitung total ringkasan (jumlah job, total fee, dibayar, sisa).
     */
    fun calculateSummary(jobs: List<Job>): RecapSummary {
        val jobCount = jobs.size
        val totalFee = jobs.sumOf { it.fee }
        val totalDibayar = jobs.sumOf { it.dibayar }
        val totalSisa = jobs.sumOf { (it.fee - it.dibayar).coerceAtLeast(0L) }

        return RecapSummary(
            jobCount = jobCount,
            totalFee = totalFee,
            totalDibayar = totalDibayar,
            totalSisa = totalSisa,
        )
    }

    /**
     * Hitung ringkasan per sumber job.
     * Diurutkan deterministik: jumlah job terbanyak -> total fee tertinggi -> nama sumber alfabetis.
     */
    fun calculateSourceRecaps(jobs: List<Job>): List<SourceRecap> {
        if (jobs.isEmpty()) return emptyList()

        return jobs.groupBy { it.sumberJob.trim() }
            .map { (source, groupJobs) ->
                val count = groupJobs.size
                val fee = groupJobs.sumOf { it.fee }
                val sisa = groupJobs.sumOf { (it.fee - it.dibayar).coerceAtLeast(0L) }
                SourceRecap(
                    sourceName = source.ifBlank { "Lainnya" },
                    jobCount = count,
                    totalFee = fee,
                    totalSisa = sisa,
                )
            }
            .sortedWith(
                compareByDescending<SourceRecap> { it.jobCount }
                    .thenByDescending { it.totalFee }
                    .thenBy { it.sourceName }
            )
    }

    /**
     * Filter daftar piutang lintas periode (semua periode).
     *
     * Kriteria:
     * - Waktu selesai job telah tercapai menurut clock saat ini
     * - Belum lunas (dibayar < fee)
     * - Fee > 0 (fee nol bukan piutang)
     * - Diurutkan dari tanggal acara terlama dulu
     */
    fun filterReceivables(jobs: List<Job>, clock: AppClock): List<Job> {
        val now = clock.nowLocalDateTime()
        val nowMinuteEpoch = now.toLocalDate().toEpochDay() * 1440L + (now.hour * 60 + now.minute)

        return jobs.filter { job ->
            val jobEndMinuteEpoch = job.tanggal * 1440L + job.jamMulaiMenit + job.durasiMenit
            val isEnded = nowMinuteEpoch >= jobEndMinuteEpoch
            val isUnpaid = job.fee > 0L && job.dibayar < job.fee
            isEnded && isUnpaid
        }.sortedWith(compareBy<Job> { it.tanggal }.thenBy { it.jamMulaiMenit })
    }
}
