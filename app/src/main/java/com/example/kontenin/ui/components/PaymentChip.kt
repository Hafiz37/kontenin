package com.example.kontenin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Chip Pembayaran Kontenin - DESIGN §5.2.
 * Bersifat informatif dan TIDAK interaktif (bukan tombol).
 * Tinggi 28 dp, latar warning-container, garis warning-accent, ikon peringatan + teks status.
 * Status tidak disampaikan hanya lewat warna (DESIGN §8.3).
 */
@Composable
fun PaymentChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val extendedColors = KonteninTheme.extendedColors
    val customTypography = KonteninTheme.customTypography
    val chipShape = RoundedCornerShape(KonteninRadius.sm)
    val statusPrefix = stringResource(R.string.cd_payment_chip_prefix)

    Box(
        modifier = modifier
            .height(KonteninTheme.dimensions.paymentChipHeight)
            .clip(chipShape)
            .background(extendedColors.warningContainer)
            .border(
                width = 1.dp,
                color = extendedColors.warningAccent,
                shape = chipShape
            )
            .padding(horizontal = 8.dp)
            .semantics(mergeDescendants = true) {
                // Informatif: hanya umumkan status, TIDAK memiliki Role.Button
                contentDescription = "$statusPrefix $text"
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null, // Sudah dirangkum di semantik kontainer
                tint = extendedColors.warningAccent,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = customTypography.caption,
                color = extendedColors.onWarning,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Helper PaymentChip otomatis berdasarkan nilai fee dan dibayar:
 * - Jika lunas (dibayar >= fee): tidak ditampilkan sama sekali (DESIGN §5.2: "hilang jika lunas").
 * - Jika dibayar == 0: teks "Belum lunas"
 * - Jika dibayar sebagian: teks "Kurang Rp X"
 */
@Composable
fun PaymentChip(
    fee: Long,
    dibayar: Long,
    modifier: Modifier = Modifier
) {
    if (fee <= 0L || dibayar >= fee) {
        // Hilang jika lunas atau fee 0 (DESIGN §5.2, FE-06)
        return
    }

    val chipText = if (dibayar <= 0L) {
        stringResource(R.string.theme_chip_warning_short) // "Belum lunas"
    } else {
        val remaining = fee - dibayar
        "Kurang Rp ${formatThousandsString(remaining.toString())}"
    }

    PaymentChip(
        text = chipText,
        modifier = modifier
    )
}

