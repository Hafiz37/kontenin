package com.example.kontenin.presentation.jobdetail

import com.example.kontenin.model.Job
import com.example.kontenin.presentation.JobDisplay

/**
 * UI State untuk Layar Detail Job (FE-13).
 */
data class JobDetailUiState(
    val isLoading: Boolean = true,
    val job: Job? = null,
    val jobDisplay: JobDisplay? = null,
    val isJobNotFound: Boolean = false,
    val isMapsButtonVisible: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val userMessage: String? = null,
)
