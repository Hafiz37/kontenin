package com.example.kontenin.ui.debug

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.contract.PermissionStatus
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Panel Kontrol Demo & Pengujian (FE-16, FE-17).
 *
 * Memfasilitasi pengujian manual dan simulasi:
 * 1. Pemicu tap notifikasi simulasi berdasarkan ID Job (valid vs dihapus).
 * 2. Toggle simulasi izin notifikasi & alarm tepat waktu.
 * 3. Pemicu simulasi cepat alur cadangan: impor 3 job, file rusak, rollback gagal, dan ekspor.
 */
@Composable
fun DemoControls(
    onSimulateNotificationTap: (jobId: Long) -> Unit,
    notificationPermissionStatus: PermissionStatus,
    exactAlarmPermissionStatus: PermissionStatus,
    onToggleNotificationPermission: () -> Unit,
    onToggleExactAlarmPermission: () -> Unit,
    modifier: Modifier = Modifier,
    onSimulateImportValid: (() -> Unit)? = null,
    onSimulateImportInvalid: (() -> Unit)? = null,
    onSimulateImportFailProcess: (() -> Unit)? = null,
    onSimulateExportSuccess: (() -> Unit)? = null,
    onSimulateExportFail: (() -> Unit)? = null,
    isBackupBusy: Boolean = false,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    var inputJobId by remember { mutableStateOf("1") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KonteninRadius.md))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(KonteninRadius.md)),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.space4),
            verticalArrangement = Arrangement.spacedBy(spacing.space3)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = colors.primary
                )
                Text(
                    text = stringResource(R.string.demo_controls_title),
                    style = KonteninTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
            }

            Text(
                text = "Simulasi Deep Link Notifikasi (EC-12):",
                style = KonteninTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                OutlinedTextField(
                    value = inputJobId,
                    onValueChange = { inputJobId = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.demo_job_id_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                KonteninPrimaryButton(
                    text = stringResource(R.string.demo_simulate_notification),
                    onClick = {
                        val id = inputJobId.toLongOrNull() ?: -1L
                        onSimulateNotificationTap(id)
                    },
                    modifier = Modifier.height(56.dp)
                )
            }

            HorizontalDivider()

            Text(
                text = "Simulasi Cepat Status Izin:",
                style = KonteninTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space2)
            ) {
                val notifLabel = if (notificationPermissionStatus == PermissionStatus.Granted) {
                    "Notif: Aktif (Ubah)"
                } else {
                    "Notif: Nonaktif (Ubah)"
                }
                KonteninOutlinedButton(
                    text = notifLabel,
                    onClick = onToggleNotificationPermission,
                    modifier = Modifier.weight(1f),
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )

                val alarmLabel = if (exactAlarmPermissionStatus == PermissionStatus.Granted) {
                    "Alarm: Aktif (Ubah)"
                } else {
                    "Alarm: Nonaktif (Ubah)"
                }
                KonteninOutlinedButton(
                    text = alarmLabel,
                    onClick = onToggleExactAlarmPermission,
                    modifier = Modifier.weight(1f),
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )
            }

            // Seksi Pengujian Cadangan Data (FE-17)
            if (onSimulateImportValid != null || onSimulateExportSuccess != null) {
                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = colors.primary
                    )
                    Text(
                        text = stringResource(R.string.demo_backup_section_title),
                        style = KonteninTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    onSimulateImportValid?.let {
                        KonteninOutlinedButton(
                            text = stringResource(R.string.demo_backup_valid_btn),
                            onClick = it,
                            enabled = !isBackupBusy,
                            modifier = Modifier.weight(1f),
                            minHeight = KonteninTheme.dimensions.minTouchTarget
                        )
                    }
                    onSimulateImportInvalid?.let {
                        KonteninOutlinedButton(
                            text = stringResource(R.string.demo_backup_invalid_btn),
                            onClick = it,
                            enabled = !isBackupBusy,
                            modifier = Modifier.weight(1f),
                            minHeight = KonteninTheme.dimensions.minTouchTarget
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.space2)
                ) {
                    onSimulateImportFailProcess?.let {
                        KonteninOutlinedButton(
                            text = stringResource(R.string.demo_backup_fail_btn),
                            onClick = it,
                            enabled = !isBackupBusy,
                            modifier = Modifier.weight(1f),
                            minHeight = KonteninTheme.dimensions.minTouchTarget
                        )
                    }
                    onSimulateExportSuccess?.let {
                        KonteninOutlinedButton(
                            text = stringResource(R.string.demo_backup_export_btn),
                            onClick = it,
                            enabled = !isBackupBusy,
                            modifier = Modifier.weight(1f),
                            minHeight = KonteninTheme.dimensions.minTouchTarget
                        )
                    }
                }
            }
        }
    }
}
