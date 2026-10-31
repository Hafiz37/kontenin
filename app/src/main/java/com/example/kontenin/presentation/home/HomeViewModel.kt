package com.example.kontenin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.JobGateway
import com.example.kontenin.util.AppClock
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * HomeViewModel untuk Kontenin - FE-07.
 * 
 * Mengelola state Beranda: observasi jobs, kategorisasi, sorting, dan filtering.
 * 
 * Logic murni ada di HomeRules.kt untuk kemudahan testing.
 */
class HomeViewModel(
    private val jobGateway: JobGateway,
    private val clock: AppClock
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        observeJobs()
    }
    
    private fun observeJobs() {
        viewModelScope.launch {
            jobGateway.observeAllJobs()
                .catch { error ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = error.message ?: "Gagal memuat data"
                    ) }
                }
                .collect { allJobs ->
                    val now = clock.now()
                    val (upcoming, history) = categorizeAndSortJobs(allJobs, now)
                    
                    _uiState.update { it.copy(
                        upcomingJobs = upcoming,
                        historyJobs = history,
                        isLoading = false,
                        error = null
                    ) }
                }
        }
    }
    
    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    fun toggleUnpaidFilter() {
        _uiState.update { state ->
            when (state.selectedTab) {
                HomeTab.UPCOMING -> state.copy(
                    showUnpaidFilterUpcoming = !state.showUnpaidFilterUpcoming
                )
                HomeTab.HISTORY -> state.copy(
                    showUnpaidFilterHistory = !state.showUnpaidFilterHistory
                )
            }
        }
    }
    
    /**
     * Re-evaluate kategori saat app resume atau waktu berubah.
     * Dipanggil dari UI saat onResume jika diperlukan.
     */
    fun refreshCategories() {
        viewModelScope.launch {
            val allJobs = jobGateway.observeAllJobs().first()
            val now = clock.now()
            val (upcoming, history) = categorizeAndSortJobs(allJobs, now)
            
            _uiState.update { it.copy(
                upcomingJobs = upcoming,
                historyJobs = history
            ) }
        }
    }
}
