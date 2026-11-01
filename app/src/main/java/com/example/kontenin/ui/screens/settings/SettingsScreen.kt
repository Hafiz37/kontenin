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
import com.example.kontenin.demo.DemoBackupFixtures
import com.example.kontenin.presentation.settings.SettingsViewModel
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.components.PermissionBanner
import com.example.kontenin.ui.debug.DemoControls
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Layar Pengaturan (FE-16, FE-17).
 *
 * Mengimplementasikan:
 * - Status izin notifikasi & alarm tepat waktu (teks + ikon, banner di Pengaturan saja)
 * - Tombol Buka Pengaturan melalui ExternalActionGateway
 * - Petunjuk baterai/autostart via BatteryHelpDialog
 * - Uji Pengingat (simulasi demo tanpa alarm OS)
 * - Ekspor & Impor Data Simulasi via BackupController & dialog (FE-17)
 * - Tampilan versi aplikasi
 * - DemoControls untuk simulasi deep link notifikasi dan cadangan data
 *
 * Acuan: PRD FR-4, FR-6, FR-7; EC-16–18; DESIGN D-4, §6.5, §7.2.
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
    val backupUiState by viewModel.backupUiState.collectAsState()
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    // ── Snackbar User Message (Pengaturan Umum / Notifikasi) ───────────────
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }

    // ── Snackbar Event Cadangan (Ekspor / Impor / Coba Lagi EC-18) ────────
    LaunchedEffect(backupUiState.snackbarEvent) {
        backupUiState.snackbarEvent?.let { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = if (event.actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
            viewModel.clearBackupSnackbar()
        }
    }

    // ── Dialog Petunjuk Baterai (FE-16) ─────────────────────────────────────
    if (uiState.showBatteryHelpDialog) {
        BatteryHelpDialog(
            onDismissRequest = { viewModel.onShowBatteryHelpDialog(false) },
            onOpenSettings = {
                viewModel.onShowBatteryHelpDialog(false)
                viewModel.onOpenSettingsClick()
            }
        )
    }

    // ── Dialog Pemilihan Tujuan Ekspor Simulasi (FE-17) ─────────────────────
    if (backupUiState.showExportDialog) {
        ExportDestinationDialog(
            suggestedFileName = backupUiState.suggestedFileName,
            onConfirm = { destination, isFail ->
                viewModel.executeExport(simulatedDestination = destination, simulateFail = isFail)
            },
            onCancel = { viewModel.onCancelExport() },
            onDismissRequest = { viewModel.dismissExportDialog() }
        )
    }

    // ── Dialog Pemilih Fixture Impor Simulasi (FE-17) ────────────────────────
    if (backupUiState.showImportChooserDialog) {
        ImportFixtureChooserDialog(
            onSelectFixture = { fixtureId ->
                viewModel.onSelectImportFixture(fixtureId)
            },
            onDismissRequest = { viewModel.dismissImportChooserDialog() }
        )
    }

    // ── Dialog Konfirmasi Penggantian Data Impor (FE-17, FR-7.2) ────────────
    if (backupUiState.showImportConfirmDialog) {
        ImportConfirmationDialog(
            jobCount = backupUiState.pendingImportJobCount,
            onConfirm = { viewModel.confirmImport() },
            onDismissRequest = { viewModel.dismissImportConfirmDialog() }
        )
    }

    // ── Dialog Indikator Proses Berjalan (>500 ms, DESIGN §7.2) ─────────────
    if (backupUiState.showProgressDialog) {
        BackupProgressDialog()
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

            // ── Seksi Data (FE-17) ──────────────────────────────────────────
            SettingsSectionHeader(title = stringResource(R.string.settings_section_data))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.space4, vertical = spacing.space2),
                verticalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                // Tombol Ekspor Data (nonaktif selama proses berlangsung - DESIGN §7.2)
                KonteninOutlinedButton(
                    text = stringResource(R.string.settings_item_export),
                    onClick = { viewModel.onExportClick() },
                    enabled = !backupUiState.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )

                // Tombol Impor Data (nonaktif selama proses berlangsung - DESIGN §7.2)
                KonteninOutlinedButton(
                    text = stringResource(R.string.settings_item_import),
                    onClick = { viewModel.onImportClick() },
                    enabled = !backupUiState.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )
            }

            HorizontalDivider(modifier = Modifier.padding(top = spacing.space2))

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

            // ── Panel Kontrol Demo & Pengujian Simulasi (FE-16, FE-17) ───────
            Spacer(modifier = Modifier.height(spacing.space3))
            DemoControls(
                onSimulateNotificationTap = onSimulateNotificationTap,
                notificationPermissionStatus = uiState.notificationPermissionStatus,
                exactAlarmPermissionStatus = uiState.exactAlarmPermissionStatus,
                onToggleNotificationPermission = { viewModel.onToggleNotificationPermission() },
                onToggleExactAlarmPermission = { viewModel.onToggleExactAlarmPermission() },
                onSimulateImportValid = {
                    viewModel.onSelectImportFixture(DemoBackupFixtures.FIXTURE_VALID_3_JOBS)
                },
                onSimulateImportInvalid = {
                    viewModel.onSelectImportFixture(DemoBackupFixtures.FIXTURE_CORRUPTED)
                },
                onSimulateImportFailProcess = {
                    viewModel.onSelectImportFixture(DemoBackupFixtures.FIXTURE_PROCESS_ERROR)
                },
                onSimulateExportSuccess = {
                    viewModel.executeExport()
                },
                onSimulateExportFail = {
                    viewModel.executeExport(simulateFail = true)
                },
                isBackupBusy = backupUiState.isBusy,
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
