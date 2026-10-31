package com.example.kontenin.presentation.recap

import com.example.kontenin.model.Job

/**
 * UI State untuk Layar Rekap (FE-15).
 */
data class RecapUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: RecapPeriod = RecapPeriod.TODAY,
    val summary: RecapSummary = RecapSummary(),
    val sourceRecaps: List<SourceRecap> = emptyList(),
    val periodJobs: List<Job> = emptyList(),
    val receivables: List<Job> = emptyList(),
    val totalReceivablesSisa: Long = 0L,
    val receivablesCount: Int = 0,
    val userMessage: String? = null,
)
