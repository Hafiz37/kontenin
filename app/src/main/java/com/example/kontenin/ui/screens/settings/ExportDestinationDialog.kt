package com.example.kontenin.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kontenin.R
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Dialog Pemilihan Tujuan Ekspor Simulasi (FE-17).
 *
 * Menampilkan:
 * - Nama saran file: `kontenin-backup-YYYYMMDD.json` (FR-7.1)
 * - Pilihan tujuan simulasi (Normal vs Simulasi Gagal)
 * - Tombol Simpan dan Batal (Pembatalan memicu alur EC-18 dengan opsi coba lagi)
 */
@Composable
fun ExportDestinationDialog(
    suggestedFileName: String,
    onConfirm: (simulatedDestination: String, simulateFail: Boolean) -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography
    var selectedOption by remember { mutableStateOf(0) } // 0: Sukses, 1: Gagal

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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.backup_export_title),
                    style = customTypography.sectionHeading,
                    color = colors.onSurface,
                    modifier = Modifier.semantics { heading() }
                )

                Text(
                    text = stringResource(R.string.backup_export_desc),
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )

                // Kotak info nama file saran
                Surface(
                    shape = RoundedCornerShape(KonteninRadius.md),
                    color = colors.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Nama saran file cadangan:",
                            style = KonteninTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = suggestedFileName,
                            style = KonteninTheme.typography.bodyLarge,
                            color = colors.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = "Pilih tujuan / skenario simulasi:",
                    style = KonteninTheme.typography.labelMedium,
                    color = colors.onSurface
                )

                // Opsi 1: Sukses
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedOption == 0),
                            onClick = { selectedOption = 0 }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == 0),
                        onClick = { selectedOption = 0 }
                    )
                    Text(
                        text = stringResource(R.string.backup_export_destination_normal),
                        style = KonteninTheme.typography.bodyMedium,
                        color = colors.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Opsi 2: Simulasi Gagal (EC-18)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedOption == 1),
                            onClick = { selectedOption = 1 }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == 1),
                        onClick = { selectedOption = 1 }
                    )
                    Text(
                        text = stringResource(R.string.backup_export_destination_fail),
                        style = KonteninTheme.typography.bodyMedium,
                        color = colors.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KonteninOutlinedButton(
                        text = stringResource(R.string.action_cancel),
                        onClick = onCancel
                    )
                    KonteninPrimaryButton(
                        text = stringResource(R.string.action_save),
                        onClick = {
                            val isFail = (selectedOption == 1)
                            onConfirm(suggestedFileName, isFail)
                        }
                    )
                }
            }
        }
    }
}
