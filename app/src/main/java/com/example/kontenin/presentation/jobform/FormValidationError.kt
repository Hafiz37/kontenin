package com.example.kontenin.presentation.jobform

import androidx.annotation.StringRes
import com.example.kontenin.R

/**
 * Representasi error validasi form ber-tipe (FE-09).
 * Setiap jenis error memetakan ke string resource di strings.xml.
 *
 * Acuan: PRD FR-1.2; DESIGN §7; Kontenin-Frontend-Tasks.md FE-09.
 */
sealed class FormValidationError(@get:StringRes val messageResId: Int) {
    // ── Nama Pengantin 1 ────────────────────────────────────────────────────
    data object NamaPengantin1Required : FormValidationError(R.string.validation_err_name_1_required)
    data object NamaPengantin1TooLong : FormValidationError(R.string.validation_err_max_50)

    // ── Nama Pengantin 2 ────────────────────────────────────────────────────
    data object NamaPengantin2Required : FormValidationError(R.string.validation_err_name_2_required)
    data object NamaPengantin2TooLong : FormValidationError(R.string.validation_err_max_50)

    // ── Waktu ───────────────────────────────────────────────────────────────
    data object TanggalRequired : FormValidationError(R.string.validation_err_date_required)
    data object JamMulaiRequired : FormValidationError(R.string.validation_err_time_required)
    data object DurasiInvalid : FormValidationError(R.string.validation_err_duration_range)

    // ── Acara dan Sumber ────────────────────────────────────────────────────
    data object AcaraRequired : FormValidationError(R.string.validation_err_event_required)
    data object AcaraTooLong : FormValidationError(R.string.validation_err_max_50)
    data object SumberJobRequired : FormValidationError(R.string.validation_err_source_required)
    data object SumberJobTooLong : FormValidationError(R.string.validation_err_max_50)

    // ── Lokasi ──────────────────────────────────────────────────────────────
    data object AlamatTooLong : FormValidationError(R.string.validation_err_address_max_300)
    data object SharelocInvalidPrefix : FormValidationError(R.string.validation_err_shareloc_prefix)

    // ── Pembayaran ──────────────────────────────────────────────────────────
    data object FeeRequired : FormValidationError(R.string.validation_err_fee_required)
    data object FeeOverflow : FormValidationError(R.string.validation_err_overflow)
    data object DibayarExceedsFee : FormValidationError(R.string.validation_err_paid_exceeds_fee)
    data object DibayarOverflow : FormValidationError(R.string.validation_err_overflow)

    // ── Catatan ─────────────────────────────────────────────────────────────
    data object CatatanTooLong : FormValidationError(R.string.validation_err_notes_max_1000)
}

/**
 * Identifikasi setiap field di Form Job untuk pemetaan error dan kontrol fokus/scroll.
 * Diurutkan berdasarkan urutan tampilan visual form dari atas ke bawah.
 */
enum class JobFormField {
    NAMA_PENGANTIN_1,
    NAMA_PENGANTIN_2,
    TANGGAL,
    JAM_MULAI,
    DURASI,
    ACARA,
    SUMBER_JOB,
    ALAMAT,
    SHARELOC,
    FEE,
    DIBAYAR,
    CATATAN
}
