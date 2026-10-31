package com.example.kontenin.presentation.jobform

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Validator murni untuk input form job (FE-09).
 *
 * Menguji seluruh aturan validasi PRD §7, FR-1.2, FR-3.2, OQ-8:
 * - Nama pengantin 1 dan 2: wajib, 1–50 karakter setelah trim.
 * - Tanggal dan jam mulai: wajib; jam 00:00–23:59.
 * - Durasi: wajib, 0,5–24 jam kelipatan 0,5 (menerima koma dan titik).
 * - Acara dan sumber job: wajib, 1–50 karakter setelah trim.
 * - Alamat: opsional, maks 300 karakter.
 * - Shareloc: opsional; jika diisi harus diawali http:// atau https://.
 * - Catatan: opsional, maks 1000 karakter.
 * - Fee: wajib, integer nonnegatif (Long), tangani overflow Long.
 * - Dibayar: opsional (kosong = 0), integer, 0 <= dibayar <= fee, tangani overflow.
 * - Tombol Lunas: aktif hanya jika fee valid dan fee > 0.
 */
object JobFormValidator {

    /**
     * Validasi nama pengantin 1.
     * Wajib 1–50 karakter setelah trim.
     */
    fun validateNamaPengantin1(value: String): FormValidationError? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> FormValidationError.NamaPengantin1Required
            trimmed.length > 50 -> FormValidationError.NamaPengantin1TooLong
            else -> null
        }
    }

    /**
     * Validasi nama pengantin 2.
     * Wajib 1–50 karakter setelah trim.
     */
    fun validateNamaPengantin2(value: String): FormValidationError? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> FormValidationError.NamaPengantin2Required
            trimmed.length > 50 -> FormValidationError.NamaPengantin2TooLong
            else -> null
        }
    }

    /**
     * Validasi tanggal acara.
     * Wajib diisi; tanggal masa lalu diperbolehkan (FR-1.1).
     */
    fun validateTanggal(epochDay: Long?): FormValidationError? {
        return if (epochDay == null) FormValidationError.TanggalRequired else null
    }

    /**
     * Validasi jam mulai acara.
     * Wajib diisi dan harus bernilai antara 0..1439 menit (00:00–23:59).
     */
    fun validateJamMulai(menit: Int?): FormValidationError? {
        return if (menit == null || menit !in 0..1439) FormValidationError.JamMulaiRequired else null
    }

    /**
     * Parse durasi dari teks yang menerima koma atau titik.
     * Valid jika 0,5 <= durasi <= 24,0 dan kelipatan 0,5.
     * Mengembalikan durasi dalam jam (Double), atau null jika tidak valid.
     */
    fun parseDurationHours(text: String): Double? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null

        val normalized = trimmed.replace(',', '.')
        val value = normalized.toDoubleOrNull() ?: return null

        if (value < 0.5 || value > 24.0) return null

        // Cek kelipatan 0,5: (value * 2) harus berupa bilangan bulat
        val doubled = value * 2.0
        val rounded = Math.round(doubled)
        if (abs(doubled - rounded) > 1e-6) return null

        return rounded / 2.0
    }

    /**
     * Parse durasi ke satuan menit untuk JobDraft.
     * Mengembalikan durasi dalam menit, atau null jika durasi tidak valid.
     */
    fun parseDurationMinutes(text: String): Int? {
        return parseDurationHours(text)?.let { (it * 60).toInt() }
    }

    /**
     * Validasi durasi (0,5–24 jam kelipatan 0,5).
     */
    fun validateDurasi(durasiText: String): FormValidationError? {
        return if (parseDurationHours(durasiText) == null) {
            FormValidationError.DurasiInvalid
        } else {
            null
        }
    }

    /**
     * Validasi acara.
     * Wajib 1–50 karakter setelah trim.
     */
    fun validateAcara(value: String): FormValidationError? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> FormValidationError.AcaraRequired
            trimmed.length > 50 -> FormValidationError.AcaraTooLong
            else -> null
        }
    }

    /**
     * Validasi sumber job.
     * Wajib 1–50 karakter setelah trim.
     */
    fun validateSumberJob(value: String): FormValidationError? {
        val trimmed = value.trim()
        return when {
            trimmed.isEmpty() -> FormValidationError.SumberJobRequired
            trimmed.length > 50 -> FormValidationError.SumberJobTooLong
            else -> null
        }
    }

    /**
     * Validasi alamat.
     * Opsional, maks 300 karakter.
     */
    fun validateAlamat(value: String): FormValidationError? {
        return if (value.length > 300) FormValidationError.AlamatTooLong else null
    }

    /**
     * Validasi shareloc.
     * Opsional; jika diisi harus diawali http:// atau https://.
     */
    fun validateShareloc(value: String): FormValidationError? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null

        val isHttp = trimmed.startsWith("http://", ignoreCase = true)
        val isHttps = trimmed.startsWith("https://", ignoreCase = true)

        return if (!isHttp && !isHttps) {
            FormValidationError.SharelocInvalidPrefix
        } else {
            null
        }
    }

    /**
     * Validasi catatan.
     * Opsional, maks 1000 karakter.
     */
    fun validateCatatan(value: String): FormValidationError? {
        return if (value.length > 1000) FormValidationError.CatatanTooLong else null
    }

    /**
     * Parse fee ke Long.
     * Mengembalikan Long jika valid, null jika kosong atau overflow.
     */
    fun parseFee(feeText: String): Long? {
        val clean = feeText.filter { it.isDigit() }
        if (clean.isEmpty()) return null
        return clean.toLongOrNull()
    }

    /**
     * Validasi fee.
     * Wajib diisi, bilangan bulat nonnegatif (>= 0), tangani overflow.
     */
    fun validateFee(feeText: String): FormValidationError? {
        val trimmed = feeText.trim()
        if (trimmed.isEmpty()) return FormValidationError.FeeRequired
        if (trimmed.startsWith('-')) return FormValidationError.FeeRequired

        val digits = trimmed.filter { it.isDigit() }
        if (digits.isEmpty()) return FormValidationError.FeeRequired

        val value = digits.toLongOrNull() ?: return FormValidationError.FeeOverflow
        return if (value < 0L) FormValidationError.FeeRequired else null
    }

    /**
     * Parse dibayar ke Long.
     * Kosong diartikan 0L. Mengembalikan null jika overflow.
     */
    fun parseDibayar(dibayarText: String): Long? {
        return com.example.kontenin.presentation.PaymentValidator.parseDibayar(dibayarText)
    }

    /**
     * Validasi kolom sudah dibayar.
     * Kosong diartikan 0. Wajib berada pada rentang 0..fee.
     * Tangani overflow dan pastikan tidak melebihi fee.
     */
    fun validateDibayar(dibayarText: String, feeText: String): FormValidationError? {
        val feeDigits = feeText.trim().filter { it.isDigit() }
        val feeValue = feeDigits.toLongOrNull() ?: Long.MAX_VALUE

        val error = com.example.kontenin.presentation.PaymentValidator.validateDibayarText(dibayarText, feeValue)
        return when (error) {
            is com.example.kontenin.presentation.PaymentValidationError.Overflow -> FormValidationError.DibayarOverflow
            is com.example.kontenin.presentation.PaymentValidationError.ExceedsFee,
            is com.example.kontenin.presentation.PaymentValidationError.Negative -> FormValidationError.DibayarExceedsFee
            null -> null
        }
    }

    /**
     * Cek apakah tombol "Lunas" aktif (enabled).
     * Sesuai OQ-8 & PRD FR-3.2: Tombol nonaktif saat fee kosong, tidak valid, atau 0.
     * Hanya aktif saat fee valid dan fee > 0.
     */
    fun isLunasEnabled(feeText: String): Boolean {
        val trimmed = feeText.trim()
        if (trimmed.isEmpty() || trimmed.startsWith('-')) return false

        val digits = trimmed.filter { it.isDigit() }
        if (digits.isEmpty()) return false

        val feeValue = digits.toLongOrNull() ?: return false
        return feeValue > 0L
    }

    /**
     * Validasi seluruh field form dan kembalikan pemetaan error terurut.
     * Urutan error sesuai urutan visual field di JobFormField.
     */
    fun validateAll(
        namaPengantin1: String,
        namaPengantin2: String,
        tanggalEpochDay: Long?,
        jamMulaiMenit: Int?,
        durasiText: String,
        acara: String,
        sumberJob: String,
        alamat: String,
        shareloc: String,
        feeText: String,
        dibayarText: String,
        catatan: String
    ): Map<JobFormField, FormValidationError> {
        val errors = linkedMapOf<JobFormField, FormValidationError>()

        validateNamaPengantin1(namaPengantin1)?.let { errors[JobFormField.NAMA_PENGANTIN_1] = it }
        validateNamaPengantin2(namaPengantin2)?.let { errors[JobFormField.NAMA_PENGANTIN_2] = it }
        validateTanggal(tanggalEpochDay)?.let { errors[JobFormField.TANGGAL] = it }
        validateJamMulai(jamMulaiMenit)?.let { errors[JobFormField.JAM_MULAI] = it }
        validateDurasi(durasiText)?.let { errors[JobFormField.DURASI] = it }
        validateAcara(acara)?.let { errors[JobFormField.ACARA] = it }
        validateSumberJob(sumberJob)?.let { errors[JobFormField.SUMBER_JOB] = it }
        validateAlamat(alamat)?.let { errors[JobFormField.ALAMAT] = it }
        validateShareloc(shareloc)?.let { errors[JobFormField.SHARELOC] = it }
        validateFee(feeText)?.let { errors[JobFormField.FEE] = it }
        validateDibayar(dibayarText, feeText)?.let { errors[JobFormField.DIBAYAR] = it }
        validateCatatan(catatan)?.let { errors[JobFormField.CATATAN] = it }

        return errors
    }
}
