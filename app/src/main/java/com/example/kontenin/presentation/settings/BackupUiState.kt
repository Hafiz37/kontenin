package com.example.kontenin.presentation.settings

/**
 * Event Snackbar untuk alur cadangan data (FE-17).
 *
 * Mendukung aksi coba lagi pada kegagalan ekspor atau pembatalan sesuai EC-18.
 */
data class BackupSnackbarEvent(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

/**
 * State UI untuk Ekspor dan Impor Data Simulasi (FE-17).
 *
 * Mengelola:
 * - Status proses (isBusy) untuk menonaktifkan kedua aksi selama proses berjalan.
 * - Tampilan dialog proses ("Memproses...") jika operasi > 500 ms (DESIGN §7.2).
 * - Dialog pemilihan tujuan ekspor dengan nama saran `kontenin-backup-YYYYMMDD.json`.
 * - Dialog pemilih fixture impor dan dialog konfirmasi penggantian data (FR-7.2).
 * - Pesan hasil snackbar dengan dukungan aksi retry.
 */
data class BackupUiState(
    val isBusy: Boolean = false,
    val showProgressDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showImportChooserDialog: Boolean = false,
    val showImportConfirmDialog: Boolean = false,
    val suggestedFileName: String = "",
    val pendingImportFixtureId: String? = null,
    val pendingImportJobCount: Int = 0,
    val snackbarEvent: BackupSnackbarEvent? = null
)
