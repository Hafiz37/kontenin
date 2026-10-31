package com.example.kontenin.util

import java.text.NumberFormat
import java.util.Locale

/**
 * RupiahFormatter untuk Kontenin - FE-06.
 * 
 * Format rupiah sesuai locale Indonesia dengan pemisah ribuan.
 * Output: "Rp 1.500.000" (dengan spasi setelah Rp).
 * 
 * Untuk angka tabular di UI, gunakan fontFeatureSettings = "tnum".
 * Pure functions tanpa state untuk kemudahan testing.
 */
object KonteninRupiahFormatter {
    
    private val LOCALE_ID = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getNumberInstance(LOCALE_ID)
    
    /**
     * Format nominal menjadi string rupiah penuh.
     * Input: 1500000
     * Output: "Rp 1.500.000"
     */
    fun formatRupiah(amount: Long): String {
        return "Rp ${currencyFormat.format(amount)}"
    }
    
    /**
     * Format rupiah dengan label prefix.
     * Input: (1500000, "Kurang")
     * Output: "Kurang Rp 1.500.000"
     */
    fun formatRupiahWithLabel(amount: Long, label: String): String {
        return "$label ${formatRupiah(amount)}"
    }
    
    /**
     * Format hanya angka tanpa prefix Rp (untuk kolom input).
     * Input: 1500000
     * Output: "1.500.000"
     */
    fun formatNumberOnly(amount: Long): String {
        return currencyFormat.format(amount)
    }
    
    /**
     * Parse string rupiah kembali ke Long.
     * Input: "1.500.000" atau "1500000"
     * Output: 1500000
     * Return null jika parsing gagal.
     */
    fun parseRupiah(input: String): Long? {
        return try {
            val cleaned = input.replace(".", "")
                .replace(",", "")
                .replace("Rp", "")
                .trim()
            cleaned.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
