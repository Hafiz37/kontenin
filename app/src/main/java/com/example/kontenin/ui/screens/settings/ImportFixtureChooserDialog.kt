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
import com.example.kontenin.demo.DemoBackupFixtures
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Dialog Pemilih File Cadangan Simulasi (FE-17).
 *
 * Menggantikan file picker SAF sistem dengan fixture cadangan demo:
 * 1. 3 Job Valid (kontenin-backup-valid-3jobs.json)
 * 2. File Rusak (kontenin-corrupted.json, EC-16)
 * 3. Skema Lebih Baru (kontenin-v99-backup.json, EC-16)
 * 4. Satu Job Tidak Valid (kontenin-invalid-job.json, EC-16)
 * 5. Gagal di Tengah Proses (kontenin-fail-midway.json, EC-17)
 */
@Composable
fun ImportFixtureChooserDialog(
    onSelectFixture: (fixtureId: String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography
    val fixtures = DemoBackupFixtures.allFixtures
    var selectedFixtureId by remember { mutableStateOf(DemoBackupFixtures.FIXTURE_VALID_3_JOBS) }

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
                .widthIn(max = 440.dp)
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.backup_import_chooser_title),
                    style = customTypography.sectionHeading,
                    color = colors.onSurface,
                    modifier = Modifier.semantics { heading() }
                )

                Text(
                    text = stringResource(R.string.backup_import_chooser_desc),
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )

                // Daftar fixture
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fixtures.forEach { fixture ->
                        val isSelected = fixture.id == selectedFixtureId
                        Surface(
                            shape = RoundedCornerShape(KonteninRadius.md),
                            color = if (isSelected) colors.primaryContainer.copy(alpha = 0.3f) else colors.surfaceVariant.copy(alpha = 0.3f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, colors.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = { selectedFixtureId = fixture.id }
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedFixtureId = fixture.id }
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = fixture.displayName,
                                        style = KonteninTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.onSurface
                                    )
                                    Text(
                                        text = fixture.fileName,
                                        style = KonteninTheme.typography.bodySmall,
                                        color = colors.primary
                                    )
                                    Text(
                                        text = fixture.description,
                                        style = KonteninTheme.typography.bodySmall,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KonteninOutlinedButton(
                        text = stringResource(R.string.action_cancel),
                        onClick = onDismissRequest
                    )
                    KonteninPrimaryButton(
                        text = stringResource(R.string.action_select),
                        onClick = { onSelectFixture(selectedFixtureId) }
                    )
                }
            }
        }
    }
}
