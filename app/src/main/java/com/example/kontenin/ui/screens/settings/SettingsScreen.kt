package com.example.kontenin.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Pengaturan (FE-05 — kerangka placeholder).
 *
 * Seksi yang akan diisi pada FE-16:
 * - Izin (notifikasi)
 * - Pengingat (jadwal, lead time)
 * - Data (ekspor, impor, hapus semua)
 * - Tentang (versi, lisensi)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val colors  = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.screen_settings),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Placeholder seksi-seksi ───────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_permission))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_notification))

            HorizontalDivider()

            SettingsSectionHeader(title = stringResource(R.string.settings_section_reminder))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_reminder_schedule))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_reminder_lead))

            HorizontalDivider()

            SettingsSectionHeader(title = stringResource(R.string.settings_section_data))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_export))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_import))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_delete_all))

            HorizontalDivider()

            SettingsSectionHeader(title = stringResource(R.string.settings_section_about))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_version))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_licenses))

            Spacer(modifier = Modifier.height(spacing.space5))

            // ── Keterangan placeholder global ─────────────────────────────────
            Text(
                text     = stringResource(R.string.placeholder_settings),
                style    = KonteninTheme.typography.bodySmall,
                color    = colors.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4),
            )
            Spacer(modifier = Modifier.height(spacing.space4))
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text     = title,
        style    = KonteninTheme.typography.labelMedium,
        color    = KonteninTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(
            start  = KonteninTheme.spacing.space4,
            end    = KonteninTheme.spacing.space4,
            top    = KonteninTheme.spacing.space4,
            bottom = KonteninTheme.spacing.space2,
        ),
    )
}

@Composable
private fun SettingsPlaceholderItem(
    label: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text  = label,
                style = KonteninTheme.typography.bodyMedium,
                color = KonteninTheme.colorScheme.onSurfaceVariant,
            )
        },
        modifier = modifier,
    )
}
