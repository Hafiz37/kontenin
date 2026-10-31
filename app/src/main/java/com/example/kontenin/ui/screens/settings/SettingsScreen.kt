package com.example.kontenin.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.contract.PermissionStatus
import com.example.kontenin.presentation.settings.SettingsUiState
import com.example.kontenin.presentation.settings.SettingsViewModel
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.components.PermissionBanner
import com.example.kontenin.ui.debug.DemoControls
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Pengaturan (FE-16).
 *
 * Mengimplementasikan:
 * - Status izin notifikasi & alarm tepat waktu (teks + ikon, banner di Pengaturan saja)
 * - Tombol Buka Pengaturan melalui ExternalActionGateway
 * - Petunjuk baterai/autostart via BatteryHelpDialog
 * - Uji Pengingat (simulasi demo tanpa alarm OS)
 * - Tampilan versi aplikasi
 * - DemoControls untuk simulasi deep link notifikasi
 *
 * Acuan: PRD FR-4, FR-6; OQ-6; DESIGN D-4, §6.5.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onSimulateNotificationTap: (jobId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    // Tampilkan snackbar jika ada user message dari ViewModel
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }

    // Tampilkan dialog petunjuk baterai jika diminta
    if (uiState.showBatteryHelpDialog) {
        BatteryHelpDialog(
            onDismissRequest = { viewModel.onShowBatteryHelpDialog(false) },
            onOpenSettings = {
                viewModel.onShowBatteryHelpDialog(false)
                viewModel.onOpenSettingsClick()
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_settings),
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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Banner Peringatan Izin (HANYA di Pengaturan sesuai D-4) ──────
            if (uiState.hasPermissionIssue) {
                PermissionBanner(
                    title = stringResource(uiState.bannerTitleRes),
                    description = stringResource(uiState.bannerDescRes),
                    actionLabel = stringResource(R.string.action_open_settings),
                    onActionClick = { viewModel.onOpenSettingsClick() },
                    modifier = Modifier.padding(spacing.space4)
                )
            }

            // ── Seksi Izin ──────────────────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_permission))

            PermissionStatusItem(
                label = stringResource(R.string.settings_item_notification),
                isGranted = uiState.isNotificationGranted,
                onOpenSettings = { viewModel.onOpenSettingsClick() }
            )

            PermissionStatusItem(
                label = stringResource(R.string.settings_item_exact_alarm),
                isGranted = uiState.isExactAlarmGranted,
                onOpenSettings = { viewModel.onOpenSettingsClick() }
            )

            HorizontalDivider()

            // ── Seksi Pengingat ─────────────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_reminder))

            // Petunjuk baterai dan autostart
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.settings_item_battery_help),
                        style = KonteninTheme.typography.bodyLarge,
                        color = colors.onSurface
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onShowBatteryHelpDialog(true) }
            )

            // Tombol Uji Pengingat (Tonal, target sentuh ≥ 48 dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4, vertical = spacing.space2),
                verticalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                KonteninTonalButton(
                    text = stringResource(R.string.settings_btn_test_reminder),
                    onClick = { viewModel.onTestReminderClick() },
                    modifier = Modifier.fillMaxWidth(),
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )

                Text(
                    text = stringResource(R.string.test_reminder_disclaimer),
                    style = KonteninTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(top = spacing.space2))

            // ── Seksi Data ──────────────────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_data))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_export))
            SettingsPlaceholderItem(label = stringResource(R.string.settings_item_import))

            HorizontalDivider()

            // ── Seksi Tentang ───────────────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_about))
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(R.string.settings_item_version),
                        style = KonteninTheme.typography.bodyLarge,
                        color = colors.onSurface
                    )
                },
                trailingContent = {
                    Text(
                        text = uiState.appVersion,
                        style = KonteninTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            )

            HorizontalDivider()

            // ── Panel Kontrol Demo & Pengujian Simulasi Notifikasi ───────────
            Spacer(modifier = Modifier.height(spacing.space3))
            DemoControls(
                onSimulateNotificationTap = onSimulateNotificationTap,
                notificationPermissionStatus = uiState.notificationPermissionStatus,
                exactAlarmPermissionStatus = uiState.exactAlarmPermissionStatus,
                onToggleNotificationPermission = { viewModel.onToggleNotificationPermission() },
                onToggleExactAlarmPermission = { viewModel.onToggleExactAlarmPermission() },
                modifier = Modifier.padding(horizontal = spacing.space4)
            )

            Spacer(modifier = Modifier.height(spacing.space5))
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = KonteninTheme.typography.labelMedium,
        color = KonteninTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(
            start = KonteninTheme.spacing.space4,
            end = KonteninTheme.spacing.space4,
            top = KonteninTheme.spacing.space4,
            bottom = KonteninTheme.spacing.space2,
        ),
    )
}

@Composable
private fun PermissionStatusItem(
    label: String,
    isGranted: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val extendedColors = KonteninTheme.extendedColors

    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = KonteninTheme.typography.bodyLarge,
                color = colors.onSurface
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isGranted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.permission_status_active),
                        style = KonteninTheme.typography.bodyMedium,
                        color = colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = extendedColors.warningAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.permission_status_inactive),
                        style = KonteninTheme.typography.bodyMedium,
                        color = extendedColors.warningAccent,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        onClick = onOpenSettings,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_open_settings),
                            style = KonteninTheme.typography.labelMedium,
                            color = colors.primary
                        )
                    }
                }
            }
        },
        modifier = modifier
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
                text = label,
                style = KonteninTheme.typography.bodyMedium,
                color = KonteninTheme.colorScheme.onSurfaceVariant,
            )
        },
        modifier = modifier,
    )
}
