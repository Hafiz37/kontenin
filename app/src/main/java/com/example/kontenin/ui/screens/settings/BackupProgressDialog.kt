package com.example.kontenin.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Dialog Indikator Proses Ekspor / Impor Cadangan (FE-17).
 *
 * Sesuai DESIGN §7.2:
 * "Dialog tak dapat dibatalkan dengan indikator bergaris dan teks 'Memproses...' hanya jika > 500 ms;
 * tombol Ekspor/Impor nonaktif selama proses."
 */
@Composable
fun BackupProgressDialog(
    modifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography

    Dialog(
        onDismissRequest = { /* Tak dapat dibatalkan saat proses (DESIGN §7.2) */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = modifier
                .padding(horizontal = KonteninTheme.dimensions.screenMargin)
                .widthIn(max = 380.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(KonteninRadius.xl),
            color = colors.surface,
            tonalElevation = KonteninTheme.dimensions.dialogElevation,
            shadowElevation = KonteninTheme.dimensions.dialogElevation
        ) {
            Column(
                modifier = Modifier
                    .padding(KonteninTheme.dimensions.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.backup_processing),
                    style = customTypography.sectionHeading,
                    color = colors.onSurface,
                    modifier = Modifier.semantics { heading() }
                )

                // Indikator bergaris (LinearProgressIndicator) sesuai DESIGN §7.2
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.primary,
                    trackColor = colors.surfaceVariant
                )
            }
        }
    }
}
