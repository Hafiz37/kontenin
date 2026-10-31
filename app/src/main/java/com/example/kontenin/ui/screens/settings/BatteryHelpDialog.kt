package com.example.kontenin.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.kontenin.R
import com.example.kontenin.ui.components.ConfirmationDialog

/**
 * Dialog Petunjuk Baterai dan Autostart (FE-16).
 *
 * Menyediakan instruksi umum (generic) untuk mematikan optimasi baterai
 * dan mengizinkan autostart agar pengingat dapat berjalan andal di latar belakang.
 * Tidak mencantumkan nama menu vendor khusus yang belum diverifikasi (D-4, EC-14).
 */
@Composable
fun BatteryHelpDialog(
    onDismissRequest: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmationDialog(
        title = stringResource(R.string.battery_help_title),
        message = stringResource(R.string.battery_help_message),
        confirmLabel = stringResource(R.string.action_open_settings),
        dismissLabel = stringResource(R.string.action_close),
        onConfirm = onOpenSettings,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    )
}
