package com.example.kontenin.presentation

import com.example.kontenin.R

/**
 * Error hasil validasi pembayaran.
 */
sealed class PaymentValidationError(val messageResId: Int) {
    data object ExceedsFee : PaymentValidationError(R.string.validation_err_paid_exceeds_fee)
    data object Overflow : PaymentValidationError(R.string.validation_err_overflow)
    data object Negative : PaymentValidationError(R.string.validation_err_paid_exceeds_fee)
}

/**
 * Validator pembayaran bersama untuk Form Job dan Detail Job (FE-14).
 *
 * Menerapkan aturan PRD FR-3, EC-7, EC-8, dan OQ-8:
 * - 0 <= dibayar <= fee
 * - Kosong diartikan 0
 * - Fee 0: lunas otomatis, tanpa chip, Tandai Lunas tidak muncul
 * - Tandai Lunas hanya muncul jika belum lunas dan fee > 0
 */
object PaymentValidator {

    /**
     * Parse teks dibayar menjadi Long.
     * Kosong diartikan 0L. Mengembalikan null jika format overflow.
     */
    fun parseDibayar(text: String): Long? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 0L
        if (trimmed.startsWith('-')) return null

        val digits = trimmed.filter { it.isDigit() }
        if (digits.isEmpty()) return 0L

        return digits.toLongOrNull()
    }

    /**
     * Validasi nominal dibayar terhadap fee.
     * Mengembalikan null jika valid (0 <= dibayar <= fee).
     */
    fun validateDibayar(dibayar: Long, fee: Long): PaymentValidationError? {
        return when {
            dibayar < 0L -> PaymentValidationError.Negative
            dibayar > fee -> PaymentValidationError.ExceedsFee
            else -> null
        }
    }

    /**
     * Validasi string teks dibayar terhadap fee.
     */
    fun validateDibayarText(dibayarText: String, fee: Long): PaymentValidationError? {
        val trimmed = dibayarText.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.startsWith('-')) return PaymentValidationError.Negative

        val digits = trimmed.filter { it.isDigit() }
        if (digits.isEmpty()) return null

        val value = digits.toLongOrNull() ?: return PaymentValidationError.Overflow
        return validateDibayar(value, fee)
    }

    /**
     * Cek apakah tombol "Tandai Lunas" harus ditampilkan di Detail Job.
     * Sesuai FR-3.3 dan testing step 4:
     * - Hanya muncul ketika belum lunas (dibayar < fee)
     * - Fee harus > 0 (fee 0 tidak menampilkan Tandai Lunas)
     */
    fun isMarkPaidVisible(fee: Long, dibayar: Long): Boolean {
        return fee > 0L && dibayar < fee
    }

    /**
     * Cek apakah status pembayaran lunas.
     */
    fun isLunas(fee: Long, dibayar: Long): Boolean {
        return fee <= 0L || dibayar >= fee
    }
}
