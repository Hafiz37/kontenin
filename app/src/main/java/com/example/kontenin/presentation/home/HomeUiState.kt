package com.example.kontenin.presentation.home

import com.example.kontenin.model.Job

/**
 * HomeUiState untuk Kontenin - FE-07.
 * 
 * State untuk halaman Beranda dengan tab Mendatang/Riwayat dan filter.
 * 
 * Keputusan Frontend:
 * - Filter state tersimpan terpisah per tab (UX lebih baik)
 * - Tidak persisten antar session (reset saat restart)
 */

data class HomeUiState(
    val selectedTab: HomeTab = HomeTab.UPCOMING,
    val showUnpaidFilterUpcoming: Boolean = false,
    val showUnpaidFilterHistory: Boolean = false,
    val upcomingJobs: List<Job> = emptyList(),
    val historyJobs: List<Job> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Jobs yang ditampilkan di tab aktif (setelah filter).
     */
    val displayedJobs: List<Job>
        get() = when (selectedTab) {
            HomeTab.UPCOMING -> {
                if (showUnpaidFilterUpcoming) {
                    upcomingJobs.filter { it.isUnpaid() }
                } else {
                    upcomingJobs
                }
            }
            HomeTab.HISTORY -> {
                if (showUnpaidFilterHistory) {
                    historyJobs.filter { it.isUnpaid() }
                } else {
                    historyJobs
                }
            }
        }
    
    /**
     * Apakah filter aktif di tab saat ini.
     */
    val isFilterActive: Boolean
        get() = when (selectedTab) {
            HomeTab.UPCOMING -> showUnpaidFilterUpcoming
            HomeTab.HISTORY -> showUnpaidFilterHistory
        }
    
    /**
     * Apakah tab kosong (untuk empty state).
     */
    val isTabEmpty: Boolean
        get() = when (selectedTab) {
            HomeTab.UPCOMING -> upcomingJobs.isEmpty()
            HomeTab.HISTORY -> historyJobs.isEmpty()
        }
    
    /**
     * Apakah hasil filter kosong (untuk empty state berbeda).
     */
    val isFilterResultEmpty: Boolean
        get() = isFilterActive && displayedJobs.isEmpty() && !isTabEmpty
}

enum class HomeTab {
    UPCOMING,
    HISTORY
}
