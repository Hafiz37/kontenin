package com.example.kontenin.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Dialog Konfirmasi Kontenin - DESIGN §5.2, §7.3, §8.4.
 * Permukaan `surface`, `radius-xl` (24 dp), padding 24 dp, elevasi 3 dp, scrim 32%.
 * Judul 18 sp SemiBold, pesan 16 sp Regular.
 * Tombol adaptif: tersusun vertikal penuh lebar bila label panjang atau font scale > 120% (DESIGN §8.4).
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.action_cancel),
    onDismiss: () -> Unit = onDismissRequest,
    isDestructive: Boolean = false,
    isLoading: Boolean = false,
    stackButtonsVertically: Boolean = false
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography
    val configuration = LocalConfiguration.current
    val scrollState = rememberScrollState()
    val titleFocusRequester = remember { FocusRequester() }

    // DESIGN §8.5: Dialog memindahkan fokus ke judul saat dibuka
    LaunchedEffect(Unit) {
        try {
            titleFocusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    // Cek apakah tombol harus tersusun vertikal (label panjang atau font scale tinggi)
    val shouldStack = stackButtonsVertically ||
            confirmLabel.length > 10 ||
            dismissLabel.length > 10 ||
            configuration.fontScale > 1.2f

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .padding(horizontal = KonteninTheme.dimensions.screenMargin)
                .widthIn(max = 420.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(KonteninRadius.xl),
            color = colors.surface,
            tonalElevation = KonteninTheme.dimensions.dialogElevation,
            shadowElevation = KonteninTheme.dimensions.dialogElevation
        ) {
            Column(
                modifier = Modifier
                    .padding(KonteninTheme.dimensions.dialogPadding)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Judul Dialog (18 sp, SemiBold 600, semantics heading untuk TalkBack - DESIGN §8.5)
                Text(
                    text = title,
                    style = customTypography.sectionHeading,
                    color = colors.onSurface,
                    modifier = Modifier
                        .focusRequester(titleFocusRequester)
                        .focusable()
                        .semantics { heading() }
                )

                // Isi Pesan Dialog (16 sp, Regular 400)
                Text(
                    text = message,
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Aksi di bagian bawah (DESIGN §5.2 & §8.4)
                if (shouldStack) {
                    // Tata letak vertikal penuh lebar (adaptif untuk font 200% & teks panjang)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(
                            // DESIGN §8.2: Jarak aksi destruktif dan aksi lain ≥ 16 dp bila vertikal
                            if (isDestructive) 16.dp else 8.dp
                        )
                    ) {
                        if (isDestructive) {
                            KonteninDestructiveButton(
                                text = confirmLabel,
                                onClick = onConfirm,
                                modifier = Modifier.fillMaxWidth(),
                                outlined = false,
                                loading = isLoading,
                                enabled = !isLoading
                            )
                            KonteninOutlinedButton(
                                text = dismissLabel,
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                        } else {
                            KonteninPrimaryButton(
                                text = confirmLabel,
                                onClick = onConfirm,
                                modifier = Modifier.fillMaxWidth(),
                                loading = isLoading,
                                enabled = !isLoading
                            )
                            KonteninOutlinedButton(
                                text = dismissLabel,
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                        }
                    }
                } else {
                    // Tata letak horizontal normal untuk label pendek dan font standar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KonteninTextButton(
                            text = dismissLabel,
                            onClick = onDismiss,
                            enabled = !isLoading
                        )

                        if (isDestructive) {
                            KonteninDestructiveButton(
                                text = confirmLabel,
                                onClick = onConfirm,
                                outlined = false,
                                loading = isLoading,
                                enabled = !isLoading
                            )
                        } else {
                            KonteninPrimaryButton(
                                text = confirmLabel,
                                onClick = onConfirm,
                                loading = isLoading,
                                enabled = !isLoading
                            )
                        }
                    }
                }
            }
        }
    }
}

