package com.example.kontenin.presentation.recap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.JobGateway
import com.example.kontenin.model.Job
import com.example.kontenin.util.AppClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Layar Rekap (FE-15).
 *
 * Mengamati seluruh data job dari gateway bersama, menghitung ringkasan pendapatan
 * per periode (Hari Ini, Minggu Ini, Bulan Ini), rekap per sumber job, dan daftar piutang.
 */
class RecapViewModel(
    private val jobGateway: JobGateway,
    private val clock: AppClock,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecapUiState())
    val uiState: StateFlow<RecapUiState> = _uiState.asStateFlow()

    private var allJobs: List<Job> = emptyList()

    init {
        observeJobs()
    }

    private fun observeJobs() {
        viewModelScope.launch(dispatcher) {
            jobGateway.observeAllJobs().collect { jobs ->
                allJobs = jobs
                recalculateState()
            }
        }
    }

    fun onPeriodSelected(period: RecapPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        _uiState.update { it.copy(selectedPeriod = period) }
        recalculateState()
    }

    private fun recalculateState() {
        val period = _uiState.value.selectedPeriod

        // Filter job sesuai periode aktif
        val periodJobs = RecapCalculator.filterJobsByPeriod(allJobs, period, clock)
        val summary = RecapCalculator.calculateSummary(periodJobs)
        val sourceRecaps = RecapCalculator.calculateSourceRecaps(periodJobs)

        // Piutang lintas periode (semua periode)
        val receivables = RecapCalculator.filterReceivables(allJobs, clock)
        val receivablesSummary = RecapCalculator.calculateSummary(receivables)

        _uiState.update {
            it.copy(
                isLoading = false,
                summary = summary,
                sourceRecaps = sourceRecaps,
                periodJobs = periodJobs,
                receivables = receivables,
                totalReceivablesSisa = receivablesSummary.totalSisa,
                receivablesCount = receivables.size,
            )
        }
    }

    fun onUserMessageConsumed() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
