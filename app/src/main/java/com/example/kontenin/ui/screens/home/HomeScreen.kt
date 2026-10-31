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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Beranda (FE-05 — kerangka placeholder).
 *
 * Fitur yang akan diisi pada FE-07:
 * - Daftar job mendatang dan riwayat
 * - Filter "Belum lunas"
 * - Navigasi ke detail job
 *
 * State yang disimpan kini:
 * - [selectedTab]: indeks tab yang aktif (rememberSaveable)
 * - [upcomingListState] / [historyListState]: posisi scroll per tab
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    onNavigateToJobDetail: (jobId: Long) -> Unit,
    onNavigateToAddJob: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── State ─────────────────────────────────────────────────────────────────
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val upcomingListState = rememberLazyListState()
    val historyListState  = rememberLazyListState()

    val tabs = listOf(
        stringResource(R.string.tab_upcoming),
        stringResource(R.string.tab_history),
    )

    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    // ── UI ───────────────────────────────────────────────────────────────────
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.screen_home),
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
                onClick            = onNavigateToAddJob,
                containerColor     = KonteninTheme.extendedColors.brand,
                contentColor       = KonteninTheme.extendedColors.onBrand,
            ) {
                Icon(
                    imageVector        = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_fab_add_job),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Tab Row ───────────────────────────────────────────────────────
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = { Text(title) },
                    )
                }
            }

            // ── Filter chip ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4, vertical = spacing.space2),
            ) {
                FilterChip(
                    selected     = false,
                    onClick      = { /* Placeholder — FE-07 */ },
                    label        = { Text(stringResource(R.string.filter_unpaid)) },
                    leadingIcon  = {
                        Icon(
                            imageVector        = Icons.Filled.FilterList,
                            contentDescription = null,
                        )
                    },
                )
            }

            // ── Konten tab ────────────────────────────────────────────────────
            val listState = if (selectedTab == 0) upcomingListState else historyListState

            LazyColumn(
                state   = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.space4),
                verticalArrangement = Arrangement.Center,
            ) {
                item {
                    Spacer(modifier = Modifier.height(spacing.space5))
                    Text(
                        text      = stringResource(R.string.placeholder_home_upcoming),
                        style     = KonteninTheme.typography.bodyMedium,
                        color     = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(spacing.space5))
                }
            }
        }
    }
}
