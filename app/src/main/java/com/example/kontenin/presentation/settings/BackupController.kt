package com.example.kontenin.presentation.settings

import com.example.kontenin.contract.BackupGateway
import com.example.kontenin.contract.ExportResult
import com.example.kontenin.contract.ImportResult
import com.example.kontenin.contract.ImportValidationResult
import com.example.kontenin.util.AppClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

/**
 * Controller untuk State Machine Ekspor dan Impor Data Cadangan Simulasi (FE-17).
 *
 * Mengelola alur:
 * 1. Ekspor: Pilih tujuan simulasi -> Proses (dialog jika > 500 ms) -> Berhasil/Gagal/Batal -> Retry (EC-18).
 * 2. Impor: Pilih fixture -> Validasi (EC-16) -> Konfirmasi Ganti Data (FR-7.2) -> Proses -> Hasil.
 * 3. Atomic replacement pada koleksi demo saat sukses, rollback tanpa data parsial saat gagal (EC-17).
 * 4. Proteksi concurrency: tombol dan aksi nonaktif selama isBusy; ketuk ganda diabaikan.
 */
class BackupController(
    private val backupGateway: BackupGateway,
    private val clock: AppClock,
    private val coroutineScope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val progressDelayMs: Long = 500L
) {

    private val _uiState = MutableStateFlow(
        BackupUiState(suggestedFileName = computeSuggestedFileName())
    )
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun computeSuggestedFileName(): String {
        val dateStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(clock.nowLocalDate())
        return "kontenin-backup-$dateStr.json"
    }

    // ── Ekspor Data ─────────────────────────────────────────────────────────

    fun onExportClick() {
        if (_uiState.value.isBusy) return
        _uiState.update {
            it.copy(
                showExportDialog = true,
                suggestedFileName = computeSuggestedFileName()
            )
        }
    }

    fun dismissExportDialog() {
        _uiState.update { it.copy(showExportDialog = false) }
    }

    fun onCancelExport() {
        dismissExportDialog()
        // Pembatalan ekspor mengikuti pesan gagal dengan opsi coba lagi sesuai EC-18
        showExportErrorWithRetry("Gagal mengekspor data")
    }

    fun executeExport(
        simulatedDestination: String? = null,
        simulateFail: Boolean = false
    ) {
        if (_uiState.value.isBusy) return
        dismissExportDialog()

        coroutineScope.launch(dispatcher) {
            runWithProgressGuard {
                if (simulateFail) {
                    showExportErrorWithRetry("Gagal mengekspor data")
                    return@runWithProgressGuard
                }

                val targetPath = simulatedDestination ?: computeSuggestedFileName()
                when (val result = backupGateway.exportToJson(targetPath)) {
                    is ExportResult.Success -> {
                        _uiState.update {
                            it.copy(
                                snackbarEvent = BackupSnackbarEvent(
                                    message = "Data berhasil diekspor (simulasi demo: ${result.path}, ${result.jobCount} job)"
                                )
                            )
                        }
                    }
                    is ExportResult.Cancelled -> {
                        showExportErrorWithRetry("Gagal mengekspor data")
                    }
                    is ExportResult.Error -> {
                        showExportErrorWithRetry("Gagal mengekspor data")
                    }
                }
            }
        }
    }

    private fun showExportErrorWithRetry(message: String) {
        _uiState.update {
            it.copy(
                snackbarEvent = BackupSnackbarEvent(
                    message = message,
                    actionLabel = "Coba lagi",
                    onAction = { onExportClick() }
                )
            )
        }
    }

    // ── Impor Data ──────────────────────────────────────────────────────────

    fun onImportClick() {
        if (_uiState.value.isBusy) return
        _uiState.update { it.copy(showImportChooserDialog = true) }
    }

    fun dismissImportChooserDialog() {
        _uiState.update { it.copy(showImportChooserDialog = false) }
    }

    fun onSelectImportFixture(fixtureId: String) {
        if (_uiState.value.isBusy) return
        dismissImportChooserDialog()

        coroutineScope.launch(dispatcher) {
            runWithProgressGuard {
                when (val validation = backupGateway.validateImport(fixtureId)) {
                    is ImportValidationResult.Invalid -> {
                        // EC-16: Ditolak dengan "File cadangan tidak valid", data lama tidak berubah
                        _uiState.update {
                            it.copy(
                                snackbarEvent = BackupSnackbarEvent(
                                    message = validation.message.ifEmpty { "File cadangan tidak valid" }
                                )
                            )
                        }
                    }
                    is ImportValidationResult.Valid -> {
                        // Tampilkan dialog konfirmasi penggantian data (FR-7.2)
                        _uiState.update {
                            it.copy(
                                showImportConfirmDialog = true,
                                pendingImportFixtureId = fixtureId,
                                pendingImportJobCount = validation.jobCount
                            )
                        }
                    }
                }
            }
        }
    }

    fun dismissImportConfirmDialog() {
        _uiState.update {
            it.copy(
                showImportConfirmDialog = false,
                pendingImportFixtureId = null,
                pendingImportJobCount = 0
            )
        }
    }

    fun confirmImport() {
        if (_uiState.value.isBusy) return
        val fixtureId = _uiState.value.pendingImportFixtureId ?: return
        dismissImportConfirmDialog()

        coroutineScope.launch(dispatcher) {
            runWithProgressGuard {
                when (val result = backupGateway.confirmImport(fixtureId)) {
                    is ImportResult.Success -> {
                        _uiState.update {
                            it.copy(
                                snackbarEvent = BackupSnackbarEvent(
                                    message = "Impor berhasil: ${result.importedCount} job"
                                )
                            )
                        }
                    }
                    is ImportResult.Error -> {
                        _uiState.update {
                            it.copy(
                                snackbarEvent = BackupSnackbarEvent(
                                    message = result.message.ifEmpty { "Gagal mengimpor data" }
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearSnackbarEvent() {
        _uiState.update { it.copy(snackbarEvent = null) }
    }

    private suspend fun runWithProgressGuard(block: suspend () -> Unit) {
        if (_uiState.value.isBusy) return
        _uiState.update { it.copy(isBusy = true) }

        var progressJob: Job? = null
        if (progressDelayMs > 0) {
            progressJob = coroutineScope.launch(dispatcher) {
                delay(progressDelayMs)
                _uiState.update { it.copy(showProgressDialog = true) }
            }
        }

        try {
            block()
        } finally {
            progressJob?.cancel()
            _uiState.update {
                it.copy(
                    isBusy = false,
                    showProgressDialog = false
                )
            }
        }
    }
}
