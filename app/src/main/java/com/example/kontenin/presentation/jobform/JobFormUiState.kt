package com.example.kontenin.presentation.jobform

import com.example.kontenin.model.JobDraft

/**
 * UI State untuk Job Form Screen (FE-08, FE-09).
 *
 * Menyimpan draft mentah input pengguna, status validasi error,
 * status tombol Lunas, dan kontrol fokus/scroll ke error pertama.
 *
 * Acuan: PRD §7, FR-1.1–1.2, FR-3.2, OQ-8; DESIGN §6.2, §7.
 */
data class JobFormUiState(
    // ── Draft input mentah ──────────────────────────────────────────────────
    val namaPengantin1: String = "",
    val namaPengantin2: String = "",
    val tanggalEpochDay: Long? = null,
    val jamMulaiMenit: Int? = null,
    val durasiText: String = "4",          // Default 4 jam, menerima koma/titik
    val acara: String = "",
    val alamat: String = "",
    val shareloc: String = "",
    val sumberJob: String = "",
    val feeText: String = "",              // Input mentah untuk parsing
    val dibayarText: String = "0",         // Default "0"
    val catatan: String = "",

    // ── Status Validasi (FE-09) ─────────────────────────────────────────────
    val errors: Map<JobFormField, FormValidationError> = emptyMap(),
    val touchedFields: Set<JobFormField> = emptySet(),
    val isLunasEnabled: Boolean = false,
    val scrollToField: JobFormField? = null, // Trigger untuk scroll & fokus saat error simpan

    // ── UI control state ────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,

    // ── Character counters (tampil saat < 20% batas) ────────────────────────
    val showAlamatCounter: Boolean = false,
    val alamatRemaining: Int = 300,
    val showCatatanCounter: Boolean = false,
    val catatanRemaining: Int = 1000,

    // ── Mode form ───────────────────────────────────────────────────────────
    val mode: FormMode = FormMode.Add,
    val sourceJobName: String? = null,      // Untuk judul duplikat dinamis
) {
    /**
     * Cek apakah counter alamat harus ditampilkan (< 20% = < 60 karakter tersisa).
     */
    fun shouldShowAlamatCounter(): Boolean = alamatRemaining < 60

    /**
     * Cek apakah counter catatan harus ditampilkan (< 20% = < 200 karakter tersisa).
     */
    fun shouldShowCatatanCounter(): Boolean = catatanRemaining < 200

    /**
     * Ambil error untuk field tertentu jika ada.
     */
    fun getError(field: JobFormField): FormValidationError? = errors[field]

    /**
     * Cek apakah field tertentu memiliki error.
     */
    fun hasError(field: JobFormField): Boolean = errors.containsKey(field)
}

/**
 * Mode operasi form job.
 */
enum class FormMode {
    Add,           // Tambah job baru
    Edit,          // Ubah job existing
    Duplicate      // Duplikat dari job existing
}

/**
 * Konversi dari JobFormUiState ke JobDraft untuk validasi/simpan (FE-09, FE-11).
 * Menggunakan helper murni dari JobFormValidator.
 */
fun JobFormUiState.toDraft(): JobDraft {
    val durasiMenit = JobFormValidator.parseDurationMinutes(durasiText)
    val fee = JobFormValidator.parseFee(feeText)
    val dibayar = JobFormValidator.parseDibayar(dibayarText) ?: 0L

    return JobDraft(
        namaPengantin1 = namaPengantin1.trim(),
        namaPengantin2 = namaPengantin2.trim(),
        tanggal = tanggalEpochDay,
        jamMulaiMenit = jamMulaiMenit,
        durasiMenit = durasiMenit,
        acara = acara.trim(),
        alamat = alamat.trim(),
        shareloc = shareloc.trim(),
        sumberJob = sumberJob.trim(),
        fee = fee,
        dibayar = dibayar,
        catatan = catatan.trim()
    )
}
