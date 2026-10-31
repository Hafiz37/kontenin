package com.example.kontenin.ui.screens.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Detail Job (FE-05 — kerangka placeholder).
 *
 * Menerima [jobId] dari argument navigasi.
 * Fitur yang akan diisi pada FE-13:
 * - Detail lengkap job (nama klien, tanggal, fee, status pembayaran, dll.)
 * - Catatan
 *
 * Desain:
 * - TIDAK ada bottom navigation (layar turunan)
 * - Top App Bar dengan tombol kembali (panah)
 * - Bottom Action Bar 80 dp: Ubah, Duplikat (primary), Hapus (error)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Long,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (jobId: Long) -> Unit,
    onNavigateToDuplicate: (jobId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors  = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val dim     = KonteninTheme.dimensions

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                title = {
                    Text(
                        text  = stringResource(R.string.screen_job_detail),
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = colors.surface,
                    navigationIconContentColor = colors.onSurface,
                    titleContentColor          = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Area scroll konten detail ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.space4),
            ) {
                Spacer(modifier = Modifier.height(spacing.space4))

                // Placeholder — konten detail diisi FE-13
                Text(
                    text      = stringResource(R.string.placeholder_job_detail),
                    style     = KonteninTheme.typography.bodyMedium,
                    color     = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(spacing.space5))
            }

            // ── Bottom Action Bar (DESIGN §5.2) ───────────────────────────────
            HorizontalDivider()
            Surface(
                color           = colors.surface,
                shadowElevation = dim.fabElevation,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(dim.bottomActionBarHeight)
                        .padding(horizontal = spacing.space4),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    // Ubah
                    ActionBarButton(
                        icon               = Icons.Filled.Edit,
                        label              = stringResource(R.string.action_bar_edit),
                        contentDescription = stringResource(R.string.action_bar_edit),
                        iconTint           = colors.onSurface,
                        onClick            = { onNavigateToEdit(jobId) },
                    )

                    // Duplikat (primary)
                    ActionBarButton(
                        icon               = Icons.Filled.ContentCopy,
                        label              = stringResource(R.string.action_bar_duplicate),
                        contentDescription = stringResource(R.string.action_bar_duplicate),
                        iconTint           = colors.primary,
                        onClick            = { onNavigateToDuplicate(jobId) },
                    )

                    // Hapus (error / destructive)
                    ActionBarButton(
                        icon               = Icons.Filled.Delete,
                        label              = stringResource(R.string.action_bar_delete),
                        contentDescription = stringResource(R.string.action_bar_delete),
                        iconTint           = colors.error,
                        onClick            = { /* Placeholder — FE-13 */ },
                    )
                }
            }
        }
    }
}

// ── Private helper ─────────────────────────────────────────────────────────────

@Composable
private fun ActionBarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    contentDescription: String,
    iconTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = KonteninTheme.spacing

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier            = modifier,
    ) {
        IconButton(
            onClick  = onClick,
            modifier = Modifier.size(KonteninTheme.dimensions.minTouchTarget),
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = contentDescription,
                tint               = iconTint,
                modifier           = Modifier.size(24.dp),
            )
        }
        Spacer(modifier = Modifier.width(spacing.space1))
        Text(
            text  = label,
            style = KonteninTheme.typography.labelSmall,
            color = iconTint,
        )
    }
}
