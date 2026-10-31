package com.example.kontenin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Banner Peringatan Izin Sistem - DESIGN §5.2.
 * Dipakai di Pengaturan saat izin notifikasi/alarm belum aktif.
 * Latar warning-container, garis warning-accent, ikon + teks + tombol aksi ≥ 48 dp.
 */
@Composable
fun PermissionBanner(
    title: String,
    description: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Notifications
) {
    val extendedColors = KonteninTheme.extendedColors
    val customTypography = KonteninTheme.customTypography
    val bannerShape = RoundedCornerShape(KonteninRadius.md)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(bannerShape)
            .background(extendedColors.warningContainer)
            .border(
                width = 1.dp,
                color = extendedColors.warningAccent,
                shape = bannerShape
            )
            .padding(16.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.cd_warning),
                    tint = extendedColors.warningAccent,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = title,
                        style = customTypography.cardTitle,
                        color = extendedColors.onWarning,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = customTypography.bodySmall,
                        color = extendedColors.onWarning
                    )
                }
            }

            // Tombol Aksi di zona bawah, target sentuh ≥ 48 dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                KonteninTonalButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    minHeight = KonteninTheme.dimensions.minTouchTarget
                )
            }
        }
    }
}

