package com.example.kontenin.ui.screens.jobform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kontenin.R
import com.example.kontenin.model.Job
import com.example.kontenin.ui.components.KonteninOutlinedButton
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme
import com.example.kontenin.util.KonteninDateTimeFormatter

/**
 * Dialog Peringatan Jadwal Bertabrakan (FE-12).
 *
 * Acuan: PRD FR-1.8; EC-4–6; DESIGN D-5, D-7, §6.2.
 * - Menampilkan daftar seluruh job yang jadwalnya tumpang tindih dengan nama, tanggal, acara, dan rentang jam.
 * - Daftar dapat digulir jika terdapat banyak konflik atau teks panjang.
 * - Tombol "Kembali" sebagai aksi utama (isi / filled), diletakkan di atas.
 * - Tombol "Tetap Simpan" sebagai aksi sekunder (outline), diletakkan di bawah.
 */
@Composable
fun ConflictDialog(
    conflicts: List<Job>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
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
                    .padding(KonteninTheme.dimensions.dialogPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Judul dengan Icon Peringatan (DESIGN §6.2)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.cd_conflict_warning),
                        tint = colors.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.conflict_dialog_title),
                        style = customTypography.sectionHeading,
                        color = colors.onSurface,
                        modifier = Modifier.semantics { heading() }
                    )
                }

                // Pengantar Pesan
                Text(
                    text = stringResource(R.string.conflict_dialog_message),
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )

                // Daftar Bentrok (Dapat digulir, mencakup nama, tanggal, acara, jam - DESIGN D-5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (job in conflicts) {
                        Surface(
                            shape = KonteninTheme.shapes.small,
                            color = colors.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                // Nama Pasangan
                                Text(
                                    text = "${job.namaPengantin1} & ${job.namaPengantin2}",
                                    style = KonteninTheme.typography.titleMedium,
                                    color = colors.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Tanggal · Acara · Rentang Jam (DESIGN D-5)
                                val dateStr = KonteninDateTimeFormatter.formatDate(job.tanggal)
                                val timeRangeStr = KonteninDateTimeFormatter.formatTimeRange(
                                    tanggal = job.tanggal,
                                    jamMulaiMenit = job.jamMulaiMenit,
                                    durasiMenit = job.durasiMenit
                                )
                                Text(
                                    text = "$dateStr · ${job.acara} · $timeRangeStr",
                                    style = KonteninTheme.typography.bodySmall,
                                    color = colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Aksi: Kembali (Utama) di atas, Tetap Simpan (Outline) di bawah (DESIGN D-7)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KonteninPrimaryButton(
                        text = stringResource(R.string.action_back),
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )

                    KonteninOutlinedButton(
                        text = stringResource(R.string.action_save_anyway),
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
