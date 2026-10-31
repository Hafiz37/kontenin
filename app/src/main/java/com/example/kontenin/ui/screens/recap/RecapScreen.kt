package com.example.kontenin.ui.screens.recap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHostState
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
 * Layar Rekap (FE-05 — kerangka placeholder).
 *
 * Fitur yang akan diisi pada FE-15:
 * - Rekap pendapatan harian / mingguan / bulanan
 * - Daftar piutang yang belum lunas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    // Simpan pilihan periode aktif
    var selectedPeriod by rememberSaveable { mutableIntStateOf(0) }

    val periods = listOf(
        stringResource(R.string.recap_period_today),
        stringResource(R.string.recap_period_week),
        stringResource(R.string.recap_period_month),
    )

    val colors  = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.screen_recap),
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = colors.surface,
                    titleContentColor = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = spacing.space4),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(modifier = Modifier.height(spacing.space4))

            // ── Segmented button periode ──────────────────────────────────────
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                periods.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape    = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = periods.size,
                        ),
                        onClick  = { selectedPeriod = index },
                        selected = selectedPeriod == index,
                        label    = { Text(label) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.space5))

            // ── Placeholder konten ────────────────────────────────────────────
            Text(
                text      = stringResource(R.string.placeholder_recap),
                style     = KonteninTheme.typography.bodyMedium,
                color     = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth(),
            )
        }
    }
}
