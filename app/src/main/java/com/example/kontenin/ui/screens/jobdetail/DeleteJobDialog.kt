package com.example.kontenin.ui.screens.jobdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.kontenin.R
import com.example.kontenin.ui.components.ConfirmationDialog

/**
 * Dialog konfirmasi hapus job sesuai FR-1.9, EC-24, dan DESIGN §6.3.
 *
 * Menegaskan bahwa job dihapus permanen dan tidak dapat dibatalkan (tidak ada undo).
 * Tidak mengklaim alarm riil dibatalkan untuk keperluan simulasi demo.
 */
@Composable
fun DeleteJobDialog(
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    isLoading: Boolean = false,
) {
    ConfirmationDialog(
        title = stringResource(R.string.delete_job_dialog_title),
        message = stringResource(R.string.delete_job_dialog_message),
        confirmLabel = stringResource(R.string.action_delete),
        dismissLabel = stringResource(R.string.action_cancel),
        onConfirm = onConfirm,
        onDismissRequest = onDismissRequest,
        isDestructive = true,
        isLoading = isLoading,
    )
}
