package com.example.kontenin.ui.screens.recap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.presentation.recap.RecapPeriod
import com.example.kontenin.presentation.recap.RecapViewModel
import com.example.kontenin.ui.components.JobCardCompact
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Rekap Pendapatan, Per Sumber Job, dan Piutang (FE-15 / PRD FR-5, DESIGN §6.4).
 *
 * Menggunakan satu daftar lazy utama dengan seksi ringkasan, per sumber job,
 * daftar job pada periode terpilih, serta daftar piutang lintas periode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(
    viewModel: RecapViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToDetail: (jobId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    LaunchedEffect(uiState.userMessage) {
        val message = uiState.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onUserMessageConsumed()
        }
    }

    val periodOptions = listOf(
        RecapPeriod.TODAY to stringResource(R.string.recap_period_today),
        RecapPeriod.THIS_WEEK to stringResource(R.string.recap_period_week),
        RecapPeriod.THIS_MONTH to stringResource(R.string.recap_period_month),
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_recap),
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Segmented button periode (48 dp) ──────────────────────────────
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4)
                    .height(48.dp),
            ) {
                periodOptions.forEachIndexed { index, (period, label) ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = periodOptions.size,
                        ),
                        onClick = { viewModel.onPeriodSelected(period) },
                        selected = uiState.selectedPeriod == period,
                        label = { Text(label) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.space3))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // ── Daftar Lazy Utama (Single Lazy Column) ────────────────────
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = spacing.space4,
                        end = spacing.space4,
                        bottom = spacing.space6,
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.space3),
                ) {
                    // 1. Kartu Ringkasan Pendapatan
                    item(key = "recap_summary_card") {
                        RecapSummaryCard(summary = uiState.summary)
                    }

                    // Pesan jika periode kosong
                    if (uiState.periodJobs.isEmpty()) {
                        item(key = "empty_period_text") {
                            Text(
                                text = stringResource(R.string.empty_recap_title),
                                style = KonteninTheme.typography.bodyMedium,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.space2),
                            )
                        }
                    }

                    // 2. Seksi Per Sumber Job (hanya muncul jika tidak kosong)
                    if (uiState.sourceRecaps.isNotEmpty()) {
                        item(key = "recap_by_source") {
                            RecapBySourceSection(sourceRecaps = uiState.sourceRecaps)
                        }
                    }

                    // 3. Seksi Daftar Job pada Periode
                    if (uiState.periodJobs.isNotEmpty()) {
                        item(key = "period_jobs_heading") {
                            Spacer(modifier = Modifier.height(spacing.space1))
                            Text(
                                text = stringResource(R.string.recap_section_job_list),
                                style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.onSurface,
                            )
                        }

                        items(
                            items = uiState.periodJobs,
                            key = { "period_job_${it.id}" },
                        ) { job ->
                            JobCardCompact(
                                job = job,
                                onClick = { onNavigateToDetail(job.id) },
                            )
                        }
                    }

                    // 4. Pembatas Seksi Piutang (semua periode)
                    item(key = "receivables_divider") {
                        Spacer(modifier = Modifier.height(spacing.space2))
                        HorizontalDivider(color = colors.outlineVariant)
                        Spacer(modifier = Modifier.height(spacing.space2))
                    }

                    // 5. Header Seksi Piutang
                    item(key = "receivables_header") {
                        ReceivablesHeader(
                            totalSisa = uiState.totalReceivablesSisa,
                            count = uiState.receivablesCount,
                        )
                    }

                    // 6. Daftar Kartu Job Piutang
                    if (uiState.receivables.isNotEmpty()) {
                        items(
                            items = uiState.receivables,
                            key = { "receivable_job_${it.id}" },
                        ) { job ->
                            JobCardCompact(
                                job = job,
                                onClick = { onNavigateToDetail(job.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
