package com.example.kontenin.ui.screens.recap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kontenin.R
import com.example.kontenin.presentation.recap.RecapSummary
import com.example.kontenin.presentation.recap.SourceRecap
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme
import com.example.kontenin.util.KonteninRupiahFormatter

/**
 * Kartu Ringkasan Pendapatan (FE-15 / DESIGN §6.4).
 */
@Composable
fun RecapSummaryCard(
    summary: RecapSummary,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Card(
        modifier = modifier.fillMaxWidth(),
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
            verticalArrangement = Arrangement.spacedBy(spacing.space2),
        ) {
            SummaryRow(
                label = stringResource(R.string.recap_total_jobs),
                value = summary.jobCount.toString(),
            )
            SummaryRow(
                label = stringResource(R.string.recap_total_fee),
                value = KonteninRupiahFormatter.formatRupiah(summary.totalFee),
            )
            SummaryRow(
                label = stringResource(R.string.recap_total_paid),
                value = KonteninRupiahFormatter.formatRupiah(summary.totalDibayar),
            )

            HorizontalDivider(
                color = colors.onPrimaryContainer.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = spacing.space1),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = stringResource(R.string.recap_total_remaining),
                    style = KonteninTheme.typography.titleMedium,
                    color = colors.onPrimaryContainer,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    text = KonteninRupiahFormatter.formatRupiah(summary.totalSisa),
                    style = KonteninTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = colors.onPrimaryContainer,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
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
            color = colors.onPrimaryContainer.copy(alpha = 0.85f),
            modifier = Modifier.weight(1f, fill = false),
        )
        Text(
            text = value,
            style = KonteninTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onPrimaryContainer,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/**
 * Seksi Ringkasan Per Sumber Job (FE-15 / DESIGN §6.4).
 * Nominal ditulis penuh tanpa singkatan.
 */
@Composable
fun RecapBySourceSection(
    sourceRecaps: List<SourceRecap>,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recap_section_by_source),
            style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space2))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.space2),
        ) {
            sourceRecaps.forEach { item ->
                SourceRecapRow(item = item)
            }
        }
    }
}

@Composable
private fun SourceRecapRow(
    item: SourceRecap,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(KonteninRadius.md))
            .padding(horizontal = spacing.space3, vertical = spacing.space2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.sourceName,
                style = KonteninTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.onSurface,
            )
            Text(
                text = stringResource(R.string.recap_source_job_count, item.jobCount),
                style = KonteninTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = KonteninRupiahFormatter.formatRupiah(item.totalFee),
                style = KonteninTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                color = colors.onSurface,
            )
            Text(
                text = stringResource(
                    R.string.recap_source_remaining,
                    KonteninRupiahFormatter.formatRupiah(item.totalSisa)
                ),
                style = KonteninTheme.typography.bodySmall,
                color = if (item.totalSisa > 0L) colors.error else colors.onSurfaceVariant,
            )
        }
    }
}

/**
 * Header Seksi Piutang (semua periode).
 */
@Composable
fun ReceivablesHeader(
    totalSisa: Long,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val colors = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.recap_section_receivables),
            style = KonteninTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onSurface,
        )
        Spacer(modifier = Modifier.height(spacing.space1))

        val summaryText = if (count > 0) {
            stringResource(
                R.string.recap_receivables_summary,
                KonteninRupiahFormatter.formatRupiah(totalSisa),
                count,
            )
        } else {
            stringResource(R.string.empty_receivables_title)
        }

        Text(
            text = summaryText,
            style = KonteninTheme.typography.bodyMedium,
            color = if (totalSisa > 0L) colors.error else colors.onSurfaceVariant,
        )
    }
}
