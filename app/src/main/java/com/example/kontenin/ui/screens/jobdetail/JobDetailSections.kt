package com.example.kontenin.ui.screens.jobdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kontenin.R
import com.example.kontenin.model.Job
import com.example.kontenin.presentation.JobDisplay
import com.example.kontenin.ui.components.PaymentChip
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Seksi-seksi tampilan data pada Layar Detail Job (FE-13, FE-18 / DESIGN §6.3, §8.4, §8.5).
 *
 * Menampilkan seluruh data pengguna tanpa mengekspos identitas teknis (ID / timestamps).
 * Diperbarui untuk mendukung aksesibilitas TalkBack, font 200%, dan layar kecil (360 dp).
 */
@Composable
fun JobDetailHeader(
    job: Job,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${job.namaPengantin1} & ${job.namaPengantin2}",
            style = KonteninTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space1))
        Text(
            text = "${job.acara} · ${job.sumberJob}",
            style = KonteninTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
fun JobDetailTimeCard(
    job: Job,
    jobDisplay: JobDisplay,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    val durationFormatted = if (job.durasiMenit % 60 == 0) {
        "${job.durasiMenit / 60}"
    } else {
        "%.1f".format(job.durasiMenit / 60.0).replace('.', ',')
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        shape = RoundedCornerShape(KonteninRadius.lg),
        colors = CardDefaults.cardColors(
            containerColor = colors.primaryContainer,
            contentColor = colors.onPrimaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.space4),
        ) {
            Text(
                text = jobDisplay.tanggalFormatted,
                style = KonteninTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = colors.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(spacing.space1))
            Text(
                text = "${jobDisplay.timeRangeFormatted} (${durationFormatted} jam)",
                style = KonteninTheme.typography.bodyMedium,
                color = colors.onPrimaryContainer,
            )
        }
    }
}

@Composable
fun JobDetailLocationSection(
    job: Job,
    isMapsButtonVisible: Boolean,
    onOpenMaps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.section_location),
            style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space2))

        val addressText = job.alamat.ifBlank {
            stringResource(R.string.detail_empty_address)
        }
        Text(
            text = addressText,
            style = KonteninTheme.typography.bodyMedium,
            color = if (job.alamat.isBlank()) colors.onSurfaceVariant else colors.onSurface,
        )

        if (isMapsButtonVisible) {
            Spacer(modifier = Modifier.height(spacing.space2))
            FilledTonalButton(
                onClick = onOpenMaps,
                shape = RoundedCornerShape(KonteninRadius.md),
                modifier = Modifier.height(KonteninTheme.dimensions.minTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(spacing.space2))
                Text(
                    text = stringResource(R.string.action_open_maps),
                    style = KonteninTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
fun JobDetailPaymentSection(
    jobDisplay: JobDisplay,
    isMarkPaidVisible: Boolean,
    onMarkPaid: () -> Unit,
    onEditPayment: () -> Unit,
    isUpdatingPayment: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val configuration = LocalConfiguration.current
    val shouldStackButtons = configuration.fontScale > 1.2f

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.section_payment),
            style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space2))

        PaymentRow(
            label = stringResource(R.string.field_fee),
            value = jobDisplay.feeFormatted,
        )
        Spacer(modifier = Modifier.height(spacing.space1))
        PaymentRow(
            label = stringResource(R.string.field_paid),
            value = jobDisplay.dibayarFormatted,
        )
        Spacer(modifier = Modifier.height(spacing.space1))
        PaymentRow(
            label = stringResource(R.string.theme_chip_warning_short),
            value = jobDisplay.sisaFormatted,
            isEmphasized = true,
        )

        jobDisplay.paymentChipText?.let { chipText ->
            Spacer(modifier = Modifier.height(spacing.space3))
            PaymentChip(text = chipText)
        }

        Spacer(modifier = Modifier.height(spacing.space3))

        // Aksi Pembayaran: Tandai Lunas (hanya bila belum lunas & fee > 0) dan Ubah Pembayaran
        // Adaptif: tersusun vertikal jika fontScale > 1.2f untuk mencegah teks terpotong (DESIGN §8.4)
        if (shouldStackButtons) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.space2),
            ) {
                if (isMarkPaidVisible) {
                    FilledTonalButton(
                        onClick = onMarkPaid,
                        enabled = !isUpdatingPayment,
                        shape = RoundedCornerShape(KonteninRadius.md),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(KonteninTheme.dimensions.minTouchTarget),
                    ) {
                        Text(
                            text = stringResource(R.string.action_mark_paid),
                            style = KonteninTheme.typography.labelLarge,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditPayment,
                    enabled = !isUpdatingPayment,
                    shape = RoundedCornerShape(KonteninRadius.md),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(KonteninTheme.dimensions.minTouchTarget),
                ) {
                    Text(
                        text = stringResource(R.string.action_edit_payment),
                        style = KonteninTheme.typography.labelLarge,
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isMarkPaidVisible) {
                    FilledTonalButton(
                        onClick = onMarkPaid,
                        enabled = !isUpdatingPayment,
                        shape = RoundedCornerShape(KonteninRadius.md),
                        modifier = Modifier.height(KonteninTheme.dimensions.minTouchTarget),
                    ) {
                        Text(
                            text = stringResource(R.string.action_mark_paid),
                            style = KonteninTheme.typography.labelLarge,
                        )
                    }
                }

                OutlinedButton(
                    onClick = onEditPayment,
                    enabled = !isUpdatingPayment,
                    shape = RoundedCornerShape(KonteninRadius.md),
                    modifier = Modifier.height(KonteninTheme.dimensions.minTouchTarget),
                ) {
                    Text(
                        text = stringResource(R.string.action_edit_payment),
                        style = KonteninTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(
    label: String,
    value: String,
    isEmphasized: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = KonteninTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.weight(1f, fill = false),
        )
        Text(
            text = value,
            style = if (isEmphasized) {
                KonteninTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            } else {
                KonteninTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            },
            color = colors.onSurface,
        )
    }
}

@Composable
fun JobDetailNotesSection(
    job: Job,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.section_notes),
            style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space2))

        val notesText = job.catatan.ifBlank {
            stringResource(R.string.detail_empty_notes)
        }
        Text(
            text = notesText,
            style = KonteninTheme.typography.bodyMedium,
            color = if (job.catatan.isBlank()) colors.onSurfaceVariant else colors.onSurface,
        )
    }
}
