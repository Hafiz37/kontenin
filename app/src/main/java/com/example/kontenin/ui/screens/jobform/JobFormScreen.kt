package com.example.kontenin.ui.screens.jobform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.kontenin.R
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Form Job (FE-05 — kerangka placeholder).
 *
 * Mendukung tiga mode melalui argument navigasi:
 * - [Routes.MODE_ADD]       → judul "Tambah Job", jobId diabaikan
 * - [Routes.MODE_EDIT]      → judul "Ubah Job", jobId = target
 * - [Routes.MODE_DUPLICATE] → judul "Ubah Job" (duplikat), jobId = sumber
 *
 * Fitur yang akan diisi pada FE-08:
 * - Field-field form job (nama klien, tanggal, fee, dll.)
 * - Validasi dan penyimpanan
 *
 * Desain:
 * - TIDAK ada bottom navigation (layar turunan)
 * - Top App Bar dengan tombol ✕ (tutup / kembali)
 * - Area scroll dengan [imePadding] agar keyboard tidak menutupi field
 * - Sticky Simpan 56 dp di bawah area scroll
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFormScreen(
    mode: String,
    jobId: Long,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors  = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val dim     = KonteninTheme.dimensions

    val screenTitle = when (mode) {
        Routes.MODE_EDIT, Routes.MODE_DUPLICATE -> stringResource(R.string.screen_edit_job)
        else                                    -> stringResource(R.string.screen_add_job)
    }

    // Gunakan Column bungkus agar sticky Simpan tidak masuk dalam scroll
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier,
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close),
                        )
                    }
                },
                title = {
                    Text(
                        text  = screenTitle,
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = colors.surface,
                    navigationIconContentColor = colors.onSurface,
                    titleContentColor      = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Area scroll konten form ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()                        // Keyboard inset
                    .padding(horizontal = spacing.space4),
            ) {
                Spacer(modifier = Modifier.height(spacing.space4))

                // Placeholder — konten form diisi FE-08
                Text(
                    text      = stringResource(R.string.placeholder_job_form),
                    style     = KonteninTheme.typography.bodyMedium,
                    color     = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(spacing.space6))
            }

            // ── Sticky Simpan — selalu terlihat di bawah, di luar scroll ─────
            Surface(
                shadowElevation = dim.fabElevation,
                color           = colors.surface,
            ) {
                KonteninPrimaryButton(
                    text     = stringResource(R.string.action_save),
                    onClick  = { /* Placeholder — FE-08 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = spacing.space4, vertical = spacing.space3),
                )
            }
        }
    }
}
