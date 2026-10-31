package com.example.kontenin.presentation.jobdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.DeleteResult
import com.example.kontenin.contract.ExternalActionGateway
import com.example.kontenin.contract.JobGateway
import com.example.kontenin.contract.OpenMapsResult
import com.example.kontenin.model.Job
import com.example.kontenin.presentation.toDisplay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Layar Detail Job (FE-13).
 *
 * Mengelola pemuatan detail data job, pembukaan Maps, dan dialog konfirmasi hapus.
 */
class JobDetailViewModel(
    private val jobGateway: JobGateway,
    private val externalActionGateway: ExternalActionGateway,
    val jobId: Long,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    private val _uiState = MutableStateFlow(JobDetailUiState())
    val uiState: StateFlow<JobDetailUiState> = _uiState.asStateFlow()

    private val brokenLinks = mutableSetOf<String>()

    init {
        observeJob()
    }

    private fun observeJob() {
        viewModelScope.launch(dispatcher) {
            jobGateway.observeJob(jobId).collect { job ->
                if (job == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            job = null,
                            jobDisplay = null,
                            isJobNotFound = true,
                            userMessage = "Job tidak ditemukan",
                        )
                    }
                } else {
                    val isLinkValid = isSharelocValid(job.shareloc)
                    val isVisible = isLinkValid && !brokenLinks.contains(job.shareloc.trim())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            job = job,
                            jobDisplay = job.toDisplay(),
                            isJobNotFound = false,
                            isMapsButtonVisible = isVisible,
                        )
                    }
                }
            }
        }
    }

    private fun isSharelocValid(shareloc: String): Boolean {
        val trimmed = shareloc.trim()
        return trimmed.isNotBlank() && (
            trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true)
        )
    }

    fun onOpenMaps() {
        val currentJob = _uiState.value.job ?: return
        val trimmedLink = currentJob.shareloc.trim()

        if (!isSharelocValid(trimmedLink)) {
            brokenLinks.add(trimmedLink)
            _uiState.update { it.copy(isMapsButtonVisible = false) }
            return
        }

        viewModelScope.launch(dispatcher) {
            when (val result = externalActionGateway.openMaps(trimmedLink)) {
                is OpenMapsResult.Success -> {
                    // Berhasil merekam event buka maps demo
                }
                is OpenMapsResult.Error -> {
                    // DESIGN D-11: sembunyikan tombol untuk nilai link tersebut
                    brokenLinks.add(trimmedLink)
                    _uiState.update {
                        it.copy(
                            isMapsButtonVisible = false,
                            userMessage = result.message.ifBlank { "Tidak ada aplikasi untuk membuka link ini" },
                        )
                    }
                }
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDeleteConfirm() {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isDeleting = true) }
            val result = jobGateway.deleteJob(jobId)
            _uiState.update { it.copy(isDeleting = false, showDeleteDialog = false) }

            when (result) {
                is DeleteResult.Success -> {
                    _uiState.update { it.copy(isDeleted = true) }
                }
                is DeleteResult.Error -> {
                    // Detail tetap terbuka, tampilkan pesan error
                    _uiState.update {
                        it.copy(
                            userMessage = result.message.ifBlank { "Gagal menghapus job" },
                        )
                    }
                }
            }
        }
    }

    fun onUserMessageConsumed() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
