package com.example.kontenin.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.kontenin.R
import com.example.kontenin.ui.components.ConfirmationDialog

/**
 * Dialog Konfirmasi Penggantian Data Impor (FE-17).
 *
 * Acuan: PRD FR-7.2; DESIGN §6.5, §7.3.
 * Konfirmasi berbunyi: "Semua data saat ini akan diganti dengan isi file (N job)."
 * Tombol utama bernada hati-hati: "Ganti Data", dan tombol sekunder "Batal".
 */
@Composable
fun ImportConfirmationDialog(
    jobCount: Int,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmationDialog(
        title = stringResource(R.string.backup_import_confirm_title),
        message = stringResource(R.string.backup_import_confirm_message, jobCount),
        confirmLabel = stringResource(R.string.action_replace_data),
        dismissLabel = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismissRequest,
        isDestructive = false, // Tombol utama bernada hati-hati (DESIGN §6.5)
        modifier = modifier
    )
}
