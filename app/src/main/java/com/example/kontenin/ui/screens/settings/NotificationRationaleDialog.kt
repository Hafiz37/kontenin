package com.example.kontenin.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kontenin.R
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Dialog Penjelasan Izin Notifikasi (FE-16).
 *
 * Ditampilkan sebelum permintaan izin saat pertama kali menyimpan job (FR-4.9, DESIGN §7.3).
 * Menyediakan aksi "Lanjut" (utama), "Nanti" (tunda), dan simulasi "Tolak".
 * Job tetap tersimpan apa pun pilihan izin yang diambil.
 */
@Composable
fun NotificationRationaleDialog(
    onContinue: () -> Unit,
    onLater: () -> Unit,
    modifier: Modifier = Modifier,
    onDeny: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = onLater,
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography
    val spacing = KonteninTheme.spacing

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp),
            shape = RoundedCornerShape(KonteninRadius.xl),
            color = colors.surface,
            tonalElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Judul
                Text(
                    text = stringResource(R.string.dialog_rationale_title),
                    style = customTypography.sectionHeading,
                    color = colors.onSurface,
                    modifier = Modifier.semantics { heading() }
                )

                // Pesan penjelasan
                Text(
                    text = stringResource(R.string.dialog_rationale_message),
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(spacing.space2))

                // Tombol aksi: Lanjut (utama), Nanti (sekunder), dan simulasi Tolak jika disediakan
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KonteninPrimaryButton(
                        text = stringResource(R.string.action_continue),
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth(),
                        minHeight = KonteninTheme.dimensions.minTouchTarget
                    )

                    KonteninOutlinedButton(
                        text = stringResource(R.string.action_later),
                        onClick = onLater,
                        modifier = Modifier.fillMaxWidth(),
                        minHeight = KonteninTheme.dimensions.minTouchTarget
                    )

                    if (onDeny != null) {
                        KonteninTonalButton(
                            text = stringResource(R.string.action_deny),
                            onClick = onDeny,
                            modifier = Modifier.fillMaxWidth(),
                            minHeight = KonteninTheme.dimensions.minTouchTarget
                        )
                    }
                }
            }
        }
    }
}
