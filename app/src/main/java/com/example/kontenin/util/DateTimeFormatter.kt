package com.example.kontenin.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * DateTimeFormatter untuk Kontenin - FE-06.
 * 
 * Format standar sesuai DESIGN D-12 dan PRD:
 * - Tanggal: "Sab, 18 Okt 2026" (dengan nama hari aktual dari kalender)
 * - Jam: "10.00" (HH.mm, angka tabular)
 * - Rentang: "10.00 - 14.00" atau "22.00 - 02.00 (19 Okt)" untuk lintas hari
 * 
 * Zona waktu: Asia/Jakarta (WIB) untuk semua waktu acara.
 * Pure functions tanpa state untuk kemudahan testing.
 */
object KonteninDateTimeFormatter {
    
    private val WIB_ZONE = ZoneId.of("Asia/Jakarta")
    private val LOCALE_ID = Locale("id", "ID")
    
    /**
     * Format tanggal dengan nama hari aktual.
     * Input: LocalDate.toEpochDay()
     * Output: "Sab, 18 Okt 2026"
     * 
     * PENTING: Nama hari dihitung dari kalender aktual, bukan hardcode.
     */
    fun formatDate(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, LOCALE_ID)
        val monthName = date.month.getDisplayName(TextStyle.SHORT, LOCALE_ID)
        
        return "$dayName, ${date.dayOfMonth} $monthName ${date.year}"
    }
    
    /**
     * Format tanggal pendek tanpa tahun (untuk rentang lintas hari).
     * Output: "19 Okt"
     */
    fun formatDateShort(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        val monthName = date.month.getDisplayName(TextStyle.SHORT, LOCALE_ID)
        
        return "${date.dayOfMonth} $monthName"
    }
    
    /**
     * Format jam dari menit sejak tengah malam.
     * Input: 0-1439 (jamMulaiMenit)
     * Output: "10.00" (HH.mm)
     */
    fun formatTime(minutesSinceMidnight: Int): String {
        val hours = minutesSinceMidnight / 60
        val minutes = minutesSinceMidnight % 60
        return String.format(LOCALE_ID, "%02d.%02d", hours, minutes)
    }
    
    /**
     * Hitung rentang waktu dari tanggal, jam mulai, dan durasi.
     * 
     * Aturan sesuai OQ-7:
     * - Jika selesai di hari yang sama: "10.00 - 14.00"
     * - Jika lintas tengah malam: "22.00 - 02.00 (19 Okt)"
     * 
     * @param tanggal LocalDate.toEpochDay()
     * @param jamMulaiMenit 0-1439
     * @param durasiMenit durasi dalam menit
     */
    fun formatTimeRange(tanggal: Long, jamMulaiMenit: Int, durasiMenit: Int): String {
        val startDate = LocalDate.ofEpochDay(tanggal)
        val startTime = LocalTime.of(jamMulaiMenit / 60, jamMulaiMenit % 60)
        
        // Hitung waktu selesai
        val startDateTime = startDate.atTime(startTime)
        val endDateTime = startDateTime.plusMinutes(durasiMenit.toLong())
        
        val startTimeStr = formatTime(jamMulaiMenit)
        val endTimeStr = String.format(
            LOCALE_ID,
            "%02d.%02d",
            endDateTime.hour,
            endDateTime.minute
        )
        
        // Cek apakah lintas hari
        return if (endDateTime.toLocalDate() != startDate) {
            // Lintas hari: tampilkan tanggal selesai
            val endDateShort = formatDateShort(endDateTime.toLocalDate().toEpochDay())
            "$startTimeStr - $endTimeStr ($endDateShort)"
        } else {
            // Hari yang sama
            "$startTimeStr - $endTimeStr"
        }
    }
    
    /**
     * Format tanggal dan rentang jam untuk kartu job.
     * Output: "Sab, 18 Okt 2026 · 10.00 - 14.00"
     */
    fun formatDateAndTimeRange(tanggal: Long, jamMulaiMenit: Int, durasiMenit: Int): String {
        val dateStr = formatDate(tanggal)
        val timeRangeStr = formatTimeRange(tanggal, jamMulaiMenit, durasiMenit)
        return "$dateStr · $timeRangeStr"
    }
    
    /**
     * Dapatkan nama hari dalam bahasa Indonesia (untuk notifikasi nanti).
     * Output: "Sab"
     */
    fun getDayName(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.dayOfWeek.getDisplayName(TextStyle.SHORT, LOCALE_ID)
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Extension functions untuk kemudahan akses (FE-06, FE-08)
// ══════════════════════════════════════════════════════════════════════════

/**
 * Format LocalDate ke string dengan nama hari.
 * Output: "Sab, 18 Okt 2026"
 */
fun formatDate(date: LocalDate): String {
    return KonteninDateTimeFormatter.formatDate(date.toEpochDay())
}

/**
 * Format waktu dari menit sejak tengah malam.
 * Output: "10.00"
 */
fun formatTime(minutesSinceMidnight: Int): String {
    return KonteninDateTimeFormatter.formatTime(minutesSinceMidnight)
}
