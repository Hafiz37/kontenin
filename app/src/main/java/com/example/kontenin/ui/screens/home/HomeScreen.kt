package com.example.kontenin.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.kontenin.R
import com.example.kontenin.presentation.home.HomeTab
import com.example.kontenin.presentation.home.HomeViewModel
import com.example.kontenin.ui.components.EmptyState
import com.example.kontenin.ui.components.JobCard
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.components.SkeletonJobList
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * HomeScreen untuk Kontenin - FE-07.
 * 
 * Implementasi lengkap Beranda dengan:
 * - Dua tab: Mendatang dan Riwayat
 * - Filter chip "Belum lunas" per tab
 * - LazyColumn dengan JobCard
 * - Empty state berbeda untuk tab kosong vs filter kosong
 * - FAB untuk tambah job
 * - State scroll dipertahankan saat navigasi
 * - Re-evaluasi kategori saat app resume
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToJobDetail: (jobId: Long) -> Unit,
    onNavigateToAddJob: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Re-evaluate kategori saat app resume (job bisa pindah kategori)
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshCategories()
        }
    }
    
    // State scroll terpisah per tab
    val upcomingListState = rememberLazyListState()
    val historyListState = rememberLazyListState()
    
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_home),
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddJob,
                containerColor = KonteninTheme.extendedColors.brand,
                contentColor = KonteninTheme.extendedColors.onBrand,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_fab_add_job),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Tab Row
            TabRow(selectedTabIndex = if (uiState.selectedTab == HomeTab.UPCOMING) 0 else 1) {
                Tab(
                    selected = uiState.selectedTab == HomeTab.UPCOMING,
                    onClick = { viewModel.selectTab(HomeTab.UPCOMING) },
                    text = { 
                        Text(
                            text = stringResource(R.string.home_tab_upcoming),
                            style = if (uiState.selectedTab == HomeTab.UPCOMING) 
                                KonteninTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                            else 
                                KonteninTheme.typography.labelLarge
                        )
                    },
                )
                Tab(
                    selected = uiState.selectedTab == HomeTab.HISTORY,
                    onClick = { viewModel.selectTab(HomeTab.HISTORY) },
                    text = { 
                        Text(
                            text = stringResource(R.string.home_tab_history),
                            style = if (uiState.selectedTab == HomeTab.HISTORY) 
                                KonteninTheme.typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                            else 
                                KonteninTheme.typography.labelLarge
                        )
                    },
                )
            }
            
            // Filter Chip "Belum lunas" (per tab)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4, vertical = spacing.space2),
            ) {
                FilterChip(
                    selected = uiState.isFilterActive,
                    onClick = { viewModel.toggleUnpaidFilter() },
                    label = { Text(stringResource(R.string.home_filter_unpaid)) },
                    leadingIcon = if (uiState.isFilterActive) {
                        {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
            
            // Content: Loading, Error, Empty, atau List
            when {
                uiState.isLoading -> {
                    // DESIGN §7.2: Tampilkan 3 kartu kerangka (skeleton) statis tanpa animasi berkedip
                    SkeletonJobList(
                        count = 3,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = spacing.space4, vertical = spacing.space2)
                    )
                }
                
                uiState.error != null -> {
                    EmptyState(
                        icon = Icons.Filled.EventBusy,
                        title = stringResource(R.string.error_load_jobs),
                        description = uiState.error,
                        actionLabel = stringResource(R.string.action_retry),
                        onActionClick = { viewModel.refreshCategories() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                uiState.isFilterResultEmpty -> {
                    // Filter aktif tapi tidak ada hasil
                    EmptyState(
                        icon = Icons.Filled.CheckCircle,
                        title = stringResource(R.string.home_empty_filter),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                uiState.isTabEmpty -> {
                    // Tab kosong (tanpa filter)
                    when (uiState.selectedTab) {
                        HomeTab.UPCOMING -> {
                            EmptyState(
                                icon = Icons.Filled.CalendarToday,
                                title = stringResource(R.string.home_empty_upcoming),
                                actionLabel = stringResource(R.string.home_action_add),
                                onActionClick = onNavigateToAddJob,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        HomeTab.HISTORY -> {
                            EmptyState(
                                icon = Icons.Filled.CalendarToday,
                                title = stringResource(R.string.home_empty_history),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                
                else -> {
                    // Tampilkan daftar job
                    val listState = if (uiState.selectedTab == HomeTab.UPCOMING) 
                        upcomingListState else historyListState
                    
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = spacing.space4),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        
                        items(
                            items = uiState.displayedJobs,
                            key = { job -> job.id }
                        ) { job ->
                            JobCard(
                                job = job,
                                onClick = { onNavigateToJobDetail(job.id) }
                            )
                        }
                        
                        // Padding bawah agar kartu terakhir tidak tertutup FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
