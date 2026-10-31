package com.example.kontenin.presentation.jobform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.ConflictCheckResult
import com.example.kontenin.contract.JobGateway
import com.example.kontenin.contract.JobResult
import com.example.kontenin.contract.ReminderGateway
import com.example.kontenin.demo.DemoReminderGateway
import com.example.kontenin.model.JobDraft
import com.example.kontenin.ui.navigation.Routes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Job Form Screen (FE-08, FE-09, FE-10).
 *
 * Mengelola draft input, validasi ber-tipe, status tombol Lunas,
 * saran autocomplete acara dan sumber job dinamis dari JobGateway,
 * normalisasi teks saat simpan, dan scroll/fokus ke error pertama.
 *
 * Acuan: PRD §7, FR-1.1–1.2, FR-2, FR-3.2, EC-23, OQ-8; DESIGN §5.2, §6.2, §7.
 */
class JobFormViewModel(
    private val jobGateway: JobGateway,
    private val savedStateHandle: SavedStateHandle,
    private val jobId: Long,
    mode: String,
    private val reminderGateway: ReminderGateway = DemoReminderGateway(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var pendingDraft: JobDraft? = null

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

        // Sync initial isLunasEnabled
        val currentFee = _uiState.value.feeText
        _uiState.update { it.copy(isLunasEnabled = JobFormValidator.isLunasEnabled(currentFee)) }

        // Observasi seluruh job dari gateway untuk autocomplete dinamis (FE-10)
        viewModelScope.launch(dispatcher) {
            jobGateway.observeAllJobs().collect { allJobs ->
                val acaraList = allJobs.map { it.acara }
                val sumberList = allJobs.map { it.sumberJob }

                _uiState.update { state ->
                    state.copy(
                        existingAcaraList = acaraList,
                        existingSumberJobList = sumberList,
                        acaraSuggestions = AutocompleteRules.getSuggestions(
                            query = state.acara,
                            existingValues = acaraList
                        ),
                        sumberJobSuggestions = AutocompleteRules.getSuggestions(
                            query = state.sumberJob,
                            existingValues = sumberList
                        )
                    )
                }
            }
        }

        // Load job data untuk mode Edit atau Duplicate
        if (mode == Routes.MODE_EDIT || mode == Routes.MODE_DUPLICATE) {
            loadJobData(jobId, mode == Routes.MODE_DUPLICATE)
        }
    }

    // ── Input Event Handlers ────────────────────────────────────────────────

    fun onNamaPengantin1Change(value: String) {
        saveToSavedState("namaPengantin1", value)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.NAMA_PENGANTIN_1) ||
                errors.containsKey(JobFormField.NAMA_PENGANTIN_1)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateNamaPengantin1(value)
                if (err != null) errors + (JobFormField.NAMA_PENGANTIN_1 to err)
                else errors - JobFormField.NAMA_PENGANTIN_1
            } else {
                errors
            }
            copy(namaPengantin1 = value, errors = updatedErrors)
        }
    }

    fun onNamaPengantin2Change(value: String) {
        saveToSavedState("namaPengantin2", value)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.NAMA_PENGANTIN_2) ||
                errors.containsKey(JobFormField.NAMA_PENGANTIN_2)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateNamaPengantin2(value)
                if (err != null) errors + (JobFormField.NAMA_PENGANTIN_2 to err)
                else errors - JobFormField.NAMA_PENGANTIN_2
            } else {
                errors
            }
            copy(namaPengantin2 = value, errors = updatedErrors)
        }
    }

    fun onTanggalSelected(epochDay: Long?) {
        epochDay?.let { saveToSavedState("tanggalEpochDay", it) }
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.TANGGAL) ||
                errors.containsKey(JobFormField.TANGGAL)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateTanggal(epochDay)
                if (err != null) errors + (JobFormField.TANGGAL to err)
                else errors - JobFormField.TANGGAL
            } else {
                errors
            }
            copy(
                tanggalEpochDay = epochDay,
                showDatePicker = false,
                errors = updatedErrors
            )
        }
    }

    fun onJamMulaiSelected(menit: Int?) {
        menit?.let { saveToSavedState("jamMulaiMenit", it) }
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.JAM_MULAI) ||
                errors.containsKey(JobFormField.JAM_MULAI)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateJamMulai(menit)
                if (err != null) errors + (JobFormField.JAM_MULAI to err)
                else errors - JobFormField.JAM_MULAI
            } else {
                errors
            }
            copy(
                jamMulaiMenit = menit,
                showTimePicker = false,
                errors = updatedErrors
            )
        }
    }

    fun onDurasiChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == ',' || it == '.' }
        saveToSavedState("durasiText", filtered)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.DURASI) ||
                errors.containsKey(JobFormField.DURASI)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateDurasi(filtered)
                if (err != null) errors + (JobFormField.DURASI to err)
                else errors - JobFormField.DURASI
            } else {
                errors
            }
            copy(durasiText = filtered, errors = updatedErrors)
        }
    }

    // ── Autocomplete Acara Handlers (FE-10) ──────────────────────────────────

    fun onAcaraChange(value: String) {
        saveToSavedState("acara", value)
        updateState {
            val suggestions = AutocompleteRules.getSuggestions(value, existingAcaraList)
            val shouldRevalidate = touchedFields.contains(JobFormField.ACARA) ||
                errors.containsKey(JobFormField.ACARA)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateAcara(value)
                if (err != null) errors + (JobFormField.ACARA to err)
                else errors - JobFormField.ACARA
            } else {
                errors
            }
            copy(
                acara = value,
                acaraSuggestions = suggestions,
                showAcaraSuggestions = suggestions.isNotEmpty(),
                errors = updatedErrors
            )
        }
    }

    fun onAcaraFocus() {
        updateState {
            val suggestions = AutocompleteRules.getSuggestions(acara, existingAcaraList)
            copy(
                acaraSuggestions = suggestions,
                showAcaraSuggestions = suggestions.isNotEmpty()
            )
        }
    }

    fun onAcaraSuggestionSelected(suggestion: String) {
        saveToSavedState("acara", suggestion)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.ACARA) ||
                errors.containsKey(JobFormField.ACARA)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateAcara(suggestion)
                if (err != null) errors + (JobFormField.ACARA to err)
                else errors - JobFormField.ACARA
            } else {
                errors
            }
            copy(
                acara = suggestion,
                showAcaraSuggestions = false,
                errors = updatedErrors
            )
        }
    }

    fun onAcaraDismissSuggestions() {
        updateState { copy(showAcaraSuggestions = false) }
    }

    // ── Autocomplete Sumber Job Handlers (FE-10) ─────────────────────────────

    fun onSumberJobChange(value: String) {
        saveToSavedState("sumberJob", value)
        updateState {
            val suggestions = AutocompleteRules.getSuggestions(value, existingSumberJobList)
            val shouldRevalidate = touchedFields.contains(JobFormField.SUMBER_JOB) ||
                errors.containsKey(JobFormField.SUMBER_JOB)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateSumberJob(value)
                if (err != null) errors + (JobFormField.SUMBER_JOB to err)
                else errors - JobFormField.SUMBER_JOB
            } else {
                errors
            }
            copy(
                sumberJob = value,
                sumberJobSuggestions = suggestions,
                showSumberJobSuggestions = suggestions.isNotEmpty(),
                errors = updatedErrors
            )
        }
    }

    fun onSumberJobFocus() {
        updateState {
            val suggestions = AutocompleteRules.getSuggestions(sumberJob, existingSumberJobList)
            copy(
                sumberJobSuggestions = suggestions,
                showSumberJobSuggestions = suggestions.isNotEmpty()
            )
        }
    }

    fun onSumberJobSuggestionSelected(suggestion: String) {
        saveToSavedState("sumberJob", suggestion)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.SUMBER_JOB) ||
                errors.containsKey(JobFormField.SUMBER_JOB)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateSumberJob(suggestion)
                if (err != null) errors + (JobFormField.SUMBER_JOB to err)
                else errors - JobFormField.SUMBER_JOB
            } else {
                errors
            }
            copy(
                sumberJob = suggestion,
                showSumberJobSuggestions = false,
                errors = updatedErrors
            )
        }
    }

    fun onSumberJobDismissSuggestions() {
        updateState { copy(showSumberJobSuggestions = false) }
    }

    // ── Lokasi, Pembayaran, dan Catatan Handlers ─────────────────────────────

    fun onAlamatChange(value: String) {
        val trimmed = value.take(300)
        val remaining = 300 - trimmed.length
        val showCounter = remaining < 60
        saveToSavedState("alamat", trimmed)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.ALAMAT) ||
                errors.containsKey(JobFormField.ALAMAT)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateAlamat(trimmed)
                if (err != null) errors + (JobFormField.ALAMAT to err)
                else errors - JobFormField.ALAMAT
            } else {
                errors
            }
            copy(
                alamat = trimmed,
                alamatRemaining = remaining,
                showAlamatCounter = showCounter,
                errors = updatedErrors
            )
        }
    }

    fun onSharelocChange(value: String) {
        saveToSavedState("shareloc", value)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.SHARELOC) ||
                errors.containsKey(JobFormField.SHARELOC)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateShareloc(value)
                if (err != null) errors + (JobFormField.SHARELOC to err)
                else errors - JobFormField.SHARELOC
            } else {
                errors
            }
            copy(shareloc = value, errors = updatedErrors)
        }
    }

    fun onFeeChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        saveToSavedState("feeText", filtered)
        val lunasEnabled = JobFormValidator.isLunasEnabled(filtered)

        updateState {
            var updatedErrors = errors

            val shouldRevalidateFee = touchedFields.contains(JobFormField.FEE) ||
                errors.containsKey(JobFormField.FEE)
            if (shouldRevalidateFee) {
                val feeErr = JobFormValidator.validateFee(filtered)
                updatedErrors = if (feeErr != null) updatedErrors + (JobFormField.FEE to feeErr)
                else updatedErrors - JobFormField.FEE
            }

            val shouldRevalidateDibayar = dibayarText.isNotBlank() ||
                touchedFields.contains(JobFormField.DIBAYAR) ||
                errors.containsKey(JobFormField.DIBAYAR)
            if (shouldRevalidateDibayar) {
                val dibayarErr = JobFormValidator.validateDibayar(dibayarText, filtered)
                updatedErrors = if (dibayarErr != null) updatedErrors + (JobFormField.DIBAYAR to dibayarErr)
                else updatedErrors - JobFormField.DIBAYAR
            }

            copy(
                feeText = filtered,
                isLunasEnabled = lunasEnabled,
                errors = updatedErrors
            )
        }
    }

    fun onDibayarChange(value: String) {
        val filtered = value.filter { it.isDigit() }
        saveToSavedState("dibayarText", filtered)
        updateState {
            val shouldRevalidate = filtered.isNotEmpty() ||
                touchedFields.contains(JobFormField.DIBAYAR) ||
                errors.containsKey(JobFormField.DIBAYAR)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateDibayar(filtered, feeText)
                if (err != null) errors + (JobFormField.DIBAYAR to err)
                else errors - JobFormField.DIBAYAR
            } else {
                errors
            }
            copy(dibayarText = filtered, errors = updatedErrors)
        }
    }

    fun onCatatanChange(value: String) {
        val trimmed = value.take(1000)
        val remaining = 1000 - trimmed.length
        val showCounter = remaining < 200
        saveToSavedState("catatan", trimmed)
        updateState {
            val shouldRevalidate = touchedFields.contains(JobFormField.CATATAN) ||
                errors.containsKey(JobFormField.CATATAN)
            val updatedErrors = if (shouldRevalidate) {
                val err = JobFormValidator.validateCatatan(trimmed)
                if (err != null) errors + (JobFormField.CATATAN to err)
                else errors - JobFormField.CATATAN
            } else {
                errors
            }
            copy(
                catatan = trimmed,
                catatanRemaining = remaining,
                showCatatanCounter = showCounter,
                errors = updatedErrors
            )
        }
    }

    // ── Field Blur Handling (FE-09) ─────────────────────────────────────────

    fun onFieldBlur(field: JobFormField) {
        updateState {
            val newTouched = touchedFields + field
            val state = this
            val fieldError: FormValidationError? = when (field) {
                JobFormField.NAMA_PENGANTIN_1 -> {
                    if (state.namaPengantin1.isNotEmpty()) JobFormValidator.validateNamaPengantin1(state.namaPengantin1) else null
                }
                JobFormField.NAMA_PENGANTIN_2 -> {
                    if (state.namaPengantin2.isNotEmpty()) JobFormValidator.validateNamaPengantin2(state.namaPengantin2) else null
                }
                JobFormField.TANGGAL -> {
                    if (state.tanggalEpochDay != null) JobFormValidator.validateTanggal(state.tanggalEpochDay) else null
                }
                JobFormField.JAM_MULAI -> {
                    if (state.jamMulaiMenit != null) JobFormValidator.validateJamMulai(state.jamMulaiMenit) else null
                }
                JobFormField.DURASI -> {
                    if (state.durasiText.isNotEmpty()) JobFormValidator.validateDurasi(state.durasiText) else null
                }
                JobFormField.ACARA -> {
                    if (state.acara.isNotEmpty()) JobFormValidator.validateAcara(state.acara) else null
                }
                JobFormField.SUMBER_JOB -> {
                    if (state.sumberJob.isNotEmpty()) JobFormValidator.validateSumberJob(state.sumberJob) else null
                }
                JobFormField.ALAMAT -> {
                    if (state.alamat.isNotEmpty()) JobFormValidator.validateAlamat(state.alamat) else null
                }
                JobFormField.SHARELOC -> {
                    if (state.shareloc.isNotEmpty()) JobFormValidator.validateShareloc(state.shareloc) else null
                }
                JobFormField.FEE -> {
                    if (state.feeText.isNotEmpty()) JobFormValidator.validateFee(state.feeText) else null
                }
                JobFormField.DIBAYAR -> {
                    if (state.dibayarText.isNotEmpty()) JobFormValidator.validateDibayar(state.dibayarText, state.feeText) else null
                }
                JobFormField.CATATAN -> {
                    if (state.catatan.isNotEmpty()) JobFormValidator.validateCatatan(state.catatan) else null
                }
            }

            val updatedErrors = if (fieldError != null) {
                errors + (field to fieldError)
            } else {
                errors - field
            }

            copy(touchedFields = newTouched, errors = updatedErrors)
        }
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
        val currentFee = _uiState.value.feeText.filter { it.isDigit() }
        if (JobFormValidator.isLunasEnabled(currentFee)) {
            saveToSavedState("dibayarText", currentFee)
            updateState {
                copy(
                    dibayarText = currentFee,
                    errors = errors - JobFormField.DIBAYAR
                )
            }
        }
    }

    fun onSaveClick() {
        if (_uiState.value.isLoading) return
        val state = _uiState.value

        // Normalisasi acara dan sumber job sebelum simpan (FE-10)
        val normalizedAcara = AutocompleteRules.normalizeForSave(state.acara, state.existingAcaraList)
        val normalizedSumberJob = AutocompleteRules.normalizeForSave(state.sumberJob, state.existingSumberJobList)

        // Perbarui input dengan hasil normalisasi
        saveToSavedState("acara", normalizedAcara)
        saveToSavedState("sumberJob", normalizedSumberJob)

        val validationErrors = JobFormValidator.validateAll(
            namaPengantin1 = state.namaPengantin1,
            namaPengantin2 = state.namaPengantin2,
            tanggalEpochDay = state.tanggalEpochDay,
            jamMulaiMenit = state.jamMulaiMenit,
            durasiText = state.durasiText,
            acara = normalizedAcara,
            sumberJob = normalizedSumberJob,
            alamat = state.alamat,
            shareloc = state.shareloc,
            feeText = state.feeText,
            dibayarText = state.dibayarText,
            catatan = state.catatan
        )

        if (validationErrors.isNotEmpty()) {
            val firstErrorField = JobFormField.entries.firstOrNull { validationErrors.containsKey(it) }

            updateState {
                copy(
                    acara = normalizedAcara,
                    sumberJob = normalizedSumberJob,
                    errors = validationErrors,
                    touchedFields = JobFormField.entries.toSet(),
                    scrollToField = firstErrorField
                )
            }
            return
        }

        // Form valid: bersihkan error dan update state dengan teks yang telah dinormalisasi
        updateState {
            copy(
                acara = normalizedAcara,
                sumberJob = normalizedSumberJob,
                errors = emptyMap(),
                scrollToField = null
            )
        }

        val draft = _uiState.value.toDraft()
        val tanggal = draft.tanggal ?: return
        val jamMulai = draft.jamMulaiMenit ?: return
        val durasi = draft.durasiMenit ?: return
        val excludeId = if (_uiState.value.mode == FormMode.Edit) jobId else null

        // Periksa jadwal bertabrakan sebelum mutasi (FE-12)
        viewModelScope.launch(dispatcher) {
            updateState { copy(isLoading = true) }
            val conflictResult = jobGateway.checkConflict(
                tanggal = tanggal,
                jamMulaiMenit = jamMulai,
                durasiMenit = durasi,
                excludeId = excludeId
            )

            when (conflictResult) {
                is ConflictCheckResult.HasConflict -> {
                    updateState {
                        copy(
                            isLoading = false,
                            showConflictDialog = true,
                            conflictingJobs = conflictResult.conflictingJobs
                        )
                    }
                }
                is ConflictCheckResult.NoConflict -> {
                    proceedSaveWithPermissionCheck(draft)
                }
            }
        }
    }

    fun onConflictDismiss() {
        if (_uiState.value.isLoading) return
        updateState {
            copy(
                showConflictDialog = false,
                conflictingJobs = emptyList()
            )
        }
    }

    fun onConflictForceSave() {
        if (_uiState.value.isLoading) return
        updateState {
            copy(
                showConflictDialog = false,
                conflictingJobs = emptyList()
            )
        }
        val draft = _uiState.value.toDraft()
        proceedSaveWithPermissionCheck(draft)
    }

    private fun proceedSaveWithPermissionCheck(draft: JobDraft) {
        viewModelScope.launch(dispatcher) {
            val permissionStatus = reminderGateway.getNotificationPermissionStatus()
            if (_uiState.value.mode != FormMode.Edit &&
                permissionStatus == com.example.kontenin.contract.PermissionStatus.NotRequested
            ) {
                pendingDraft = draft
                updateState {
                    copy(
                        isLoading = false,
                        showNotificationRationaleDialog = true
                    )
                }
            } else {
                saveJobInternal(draft)
            }
        }
    }

    fun onRationaleContinue() {
        val draft = pendingDraft ?: _uiState.value.toDraft()
        pendingDraft = null
        updateState { copy(showNotificationRationaleDialog = false) }
        viewModelScope.launch(dispatcher) {
            reminderGateway.requestNotificationPermission()
            saveJobInternal(draft)
        }
    }

    fun onRationaleLater() {
        val draft = pendingDraft ?: _uiState.value.toDraft()
        pendingDraft = null
        updateState { copy(showNotificationRationaleDialog = false) }
        viewModelScope.launch(dispatcher) {
            saveJobInternal(draft)
        }
    }

    fun onRationaleDeny() {
        val draft = pendingDraft ?: _uiState.value.toDraft()
        pendingDraft = null
        updateState { copy(showNotificationRationaleDialog = false) }
        viewModelScope.launch(dispatcher) {
            if (reminderGateway is com.example.kontenin.demo.DemoReminderGateway) {
                reminderGateway.setNotificationPermission(com.example.kontenin.contract.PermissionStatus.Denied)
            }
            saveJobInternal(draft)
        }
    }

    private suspend fun saveJobInternal(draft: JobDraft) {
        updateState { copy(isLoading = true) }
        val result = when (_uiState.value.mode) {
            FormMode.Edit -> jobGateway.updateJob(jobId, draft)
            else -> jobGateway.saveJob(draft)
        }

        when (result) {
            is JobResult.Success -> {
                val tanggal = draft.tanggal ?: 0L
                val jamMulai = draft.jamMulaiMenit ?: 0
                val reminderResult = reminderGateway.scheduleJobReminders(tanggal, jamMulai)
                updateState {
                    copy(
                        isLoading = false,
                        isSaved = true,
                        hasSkippedReminders = reminderResult.skippedCount > 0,
                        skippedRemindersCount = reminderResult.skippedCount
                    )
                }
            }
            is JobResult.Error -> {
                updateState {
                    copy(
                        isLoading = false,
                        saveErrorMessage = result.message
                    )
                }
            }
        }
    }

    fun onScrollToFieldConsumed() {
        updateState { copy(scrollToField = null) }
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private fun loadJobData(id: Long, isDuplicate: Boolean) {
        viewModelScope.launch(dispatcher) {
            val job = jobGateway.observeJob(id).firstOrNull() ?: return@launch
            val durasiHours = job.durasiMenit / 60.0
            val durasiStr = if (durasiHours % 1.0 == 0.0) {
                durasiHours.toInt().toString()
            } else {
                durasiHours.toString().replace('.', ',')
            }

            if (isDuplicate) {
                // Duplikat menyalin nama, jam mulai, durasi, alamat, shareloc, sumber, fee, catatan;
                // tanggal dan acara kosong, dibayar 0.
                updateState {
                    copy(
                        namaPengantin1 = job.namaPengantin1,
                        namaPengantin2 = job.namaPengantin2,
                        tanggalEpochDay = null,
                        jamMulaiMenit = job.jamMulaiMenit,
                        durasiText = durasiStr,
                        acara = "",
                        alamat = job.alamat,
                        shareloc = job.shareloc,
                        sumberJob = job.sumberJob,
                        feeText = job.fee.toString(),
                        dibayarText = "0",
                        catatan = job.catatan,
                        sourceJobName = "${job.namaPengantin1} & ${job.namaPengantin2}",
                        isLunasEnabled = JobFormValidator.isLunasEnabled(job.fee.toString())
                    )
                }
            } else {
                // Ubah mempertahankan seluruh field
                updateState {
                    copy(
                        namaPengantin1 = job.namaPengantin1,
                        namaPengantin2 = job.namaPengantin2,
                        tanggalEpochDay = job.tanggal,
                        jamMulaiMenit = job.jamMulaiMenit,
                        durasiText = durasiStr,
                        acara = job.acara,
                        alamat = job.alamat,
                        shareloc = job.shareloc,
                        sumberJob = job.sumberJob,
                        feeText = job.fee.toString(),
                        dibayarText = job.dibayar.toString(),
                        catatan = job.catatan,
                        isLunasEnabled = JobFormValidator.isLunasEnabled(job.fee.toString())
                    )
                }
            }
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

        val lunasEnabled = JobFormValidator.isLunasEnabled(feeText)

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
                showCatatanCounter = (1000 - catatan.length) < 200,
                isLunasEnabled = lunasEnabled
            )
        }
    }
}
