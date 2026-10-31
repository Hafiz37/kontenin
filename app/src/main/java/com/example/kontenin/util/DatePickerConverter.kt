package com.example.kontenin.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * DatePickerConverter untuk Kontenin - FE-06.
 * 
 * Konversi timestamp UTC dari Material DatePicker menjadi LocalDate.
 * 
 * PENTING (sesuai EC-22 dan brief):
 * DatePicker Material 3 mengembalikan milidetik UTC tengah malam.
 * Konversi HARUS memakai UTC, bukan Asia/Jakarta, agar tanggal tidak mundur satu hari.
 * 
 * Contoh:
 * - Input: 1730332800000 (2024-10-31T00:00:00Z UTC)
 * - Output: LocalDate(2024, 10, 31)
 * - BUKAN: LocalDate(2024, 10, 30) ← salah jika pakai Asia/Jakarta
 * 
 * Pure functions dengan unit test untuk batas bulan/tahun.
 */
object KonteninDatePickerConverter {
    
    private val UTC_ZONE = ZoneId.of("UTC")
    
    /**
     * Konversi UTC millis dari DatePicker ke LocalDate.
     * 
     * @param utcMillis timestamp UTC tengah malam dari DatePicker
     * @return LocalDate yang sesuai (tidak bergeser)
     */
    fun convertPickerToLocalDate(utcMillis: Long): LocalDate {
        val instant = Instant.ofEpochMilli(utcMillis)
        return instant.atZone(UTC_ZONE).toLocalDate()
    }
    
    /**
     * Konversi LocalDate ke UTC millis untuk DatePicker (kebalikannya).
     * 
     * @param date LocalDate yang ingin diset di picker
     * @return UTC millis tengah malam
     */
    fun convertLocalDateToPicker(date: LocalDate): Long {
        return date.atStartOfDay(UTC_ZONE).toInstant().toEpochMilli()
    }
    
    /**
     * Konversi LocalDate ke epoch day untuk disimpan di database.
     * 
     * @param date LocalDate dari picker
     * @return epoch day (Long) untuk field Job.tanggal
     */
    fun convertToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }
}
