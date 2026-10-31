package com.example.kontenin.presentation.jobform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.JobGateway
import com.example.kontenin.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Job Form Screen (FE-08).
 *
 * Mengelola draft input, character counters, date/time picker state,
 * dan mempertahankan draft saat configuration change melalui SavedStateHandle.
 *
 * Acuan: PRD §7, FR-1.1–1.2; DESIGN §6.2.
 * Prasyarat: FE-02 (JobGateway), FE-05 (navigation mode), FE-07 (selesai).
 */
class JobFormViewModel(
    private val jobGateway: JobGateway,
    private val savedStateHandle: SavedStateHandle,
    private val jobId: Long,
    mode: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        JobFormUiState(
            mode = when (mode) {
                Routes.MODE_EDIT -> FormMode.Edit
                Routes.MODE_DUPLICATE -> FormMode.Duplicate
                else -> FormMode.Add
            }
        )
    )
    val uiState: StateFlow<JobFormUiState> = _uiState.asStateFlow()

    init {
        // Restore state dari SavedStateHandle jika ada
        restoreFromSavedState()

        // Load job data untuk mode Edit atau Duplicate
        if (mode == Routes.MODE_EDIT || mode == Routes.MODE_DUPLICATE) {
            loadJobData(jobId, mode == Routes.MODE_DUPLICATE)
        }
    }

    // ── Input Event Handlers ────────────────────────────────────────────────

    fun onNamaPengantin1Change(value: String) {
        updateState { copy(namaPengantin1 = value) }
        saveToSavedState("namaPengantin1", value)
    }

    fun onNamaPengantin2Change(value: String) {
        updateState { copy(namaPengantin2 = value) }
        saveToSavedState("namaPengantin2", value)
    }

    fun onTanggalSelected(epochDay: Long?) {
        updateState { copy(tanggalEpochDay = epochDay, showDatePicker = false) }
        epochDay?.let { saveToSavedState("tanggalEpochDay", it) }
    }

    fun onJamMulaiSelected(menit: Int?) {
        updateState { copy(jamMulaiMenit = menit, showTimePicker = false) }
        menit?.let { saveToSavedState("jamMulaiMenit", it) }
    }

    fun onDurasiChange(value: String) {
        // Terima angka, koma, titik
        val filtered = value.filter { it.isDigit() || it == ',' || it == '.' }
        updateState { copy(durasiText = filtered) }
        saveToSavedState("durasiText", filtered)
    }

    fun onAcaraChange(value: String) {
        updateState { copy(acara = value) }
        saveToSavedState("acara", value)
    }

    fun onAlamatChange(value: String) {
        val trimmed = value.take(300)
        val remaining = 300 - trimmed.length
        val showCounter = remaining < 60
        updateState {
            copy(
                alamat = trimmed,
                alamatRemaining = remaining,
                showAlamatCounter = showCounter
            )
        }
        saveToSavedState("alamat", trimmed)
    }

    fun onSharelocChange(value: String) {
        updateState { copy(shareloc = value) }
        saveToSavedState("shareloc", value)
    }

    fun onSumberJobChange(value: String) {
        updateState { copy(sumberJob = value) }
        saveToSavedState("sumberJob", value)
    }

    fun onFeeChange(value: String) {
        // Hanya digit
        val filtered = value.filter { it.isDigit() }
        updateState { copy(feeText = filtered) }
        saveToSavedState("feeText", filtered)
    }

    fun onDibayarChange(value: String) {
        // Hanya digit
        val filtered = value.filter { it.isDigit() }
        updateState { copy(dibayarText = filtered) }
        saveToSavedState("dibayarText", filtered)
    }

    fun onCatatanChange(value: String) {
        val trimmed = value.take(1000)
        val remaining = 1000 - trimmed.length
        val showCounter = remaining < 200
        updateState {
            copy(
                catatan = trimmed,
                catatanRemaining = remaining,
                showCatatanCounter = showCounter
            )
        }
        saveToSavedState("catatan", trimmed)
    }

    // ── UI Control Actions ──────────────────────────────────────────────────

    fun showDatePicker() {
        updateState { copy(showDatePicker = true) }
    }

    fun hideDatePicker() {
        updateState { copy(showDatePicker = false) }
    }

    fun showTimePicker() {
        updateState { copy(showTimePicker = true) }
    }

    fun hideTimePicker() {
        updateState { copy(showTimePicker = false) }
    }

    fun onLunasClick() {
        // Tombol Lunas: set dibayar = fee (FE-09 akan handle validasi)
        val currentFee = _uiState.value.feeText
        if (currentFee.isNotBlank()) {
            updateState { copy(dibayarText = currentFee) }
            saveToSavedState("dibayarText", currentFee)
        }
    }

    fun onSaveClick() {
        // TODO FE-11: validasi dan simpan ke gateway
        // Placeholder untuk FE-08
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private fun loadJobData(id: Long, isDuplicate: Boolean) {
        viewModelScope.launch {
            // TODO FE-11: load dari gateway
            // Placeholder untuk FE-08
        }
    }

    private fun updateState(block: JobFormUiState.() -> JobFormUiState) {
        _uiState.update(block)
    }

    private fun saveToSavedState(key: String, value: Any) {
        savedStateHandle[key] = value
    }

    private fun restoreFromSavedState() {
        val namaPengantin1 = savedStateHandle.get<String>("namaPengantin1") ?: ""
        val namaPengantin2 = savedStateHandle.get<String>("namaPengantin2") ?: ""
        val tanggalEpochDay = savedStateHandle.get<Long>("tanggalEpochDay")
        val jamMulaiMenit = savedStateHandle.get<Int>("jamMulaiMenit")
        val durasiText = savedStateHandle.get<String>("durasiText") ?: "4"
        val acara = savedStateHandle.get<String>("acara") ?: ""
        val alamat = savedStateHandle.get<String>("alamat") ?: ""
        val shareloc = savedStateHandle.get<String>("shareloc") ?: ""
        val sumberJob = savedStateHandle.get<String>("sumberJob") ?: ""
        val feeText = savedStateHandle.get<String>("feeText") ?: ""
        val dibayarText = savedStateHandle.get<String>("dibayarText") ?: "0"
        val catatan = savedStateHandle.get<String>("catatan") ?: ""

        // Update state dengan restored values
        _uiState.update {
            it.copy(
                namaPengantin1 = namaPengantin1,
                namaPengantin2 = namaPengantin2,
                tanggalEpochDay = tanggalEpochDay,
                jamMulaiMenit = jamMulaiMenit,
                durasiText = durasiText,
                acara = acara,
                alamat = alamat,
                shareloc = shareloc,
                sumberJob = sumberJob,
                feeText = feeText,
                dibayarText = dibayarText,
                catatan = catatan,
                alamatRemaining = 300 - alamat.length,
                showAlamatCounter = (300 - alamat.length) < 60,
                catatanRemaining = 1000 - catatan.length,
                showCatatanCounter = (1000 - catatan.length) < 200
            )
        }
    }
}
