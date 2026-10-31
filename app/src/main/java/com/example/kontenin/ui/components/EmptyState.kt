package com.example.kontenin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Empty State Kontenin - DESIGN §7.1.
 * Format: Ikon garis sederhana 48 dp warna on-surface-variant + satu kalimat judul (16 sp SemiBold)
 * + satu kalimat penjelas (14 sp Regular) + tombol aksi tonal opsional di zona tengah-bawah.
 * Tanpa ilustrasi dekoratif.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon garis sederhana 48 dp (DESIGN §7.1)
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Judul (16 sp, SemiBold 600)
        Text(
            text = title,
            style = customTypography.cardTitle,
            color = colors.onSurface,
            textAlign = TextAlign.Center
        )

        // Penjelas (14 sp, Regular 400)
        if (!description.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = customTypography.bodySmall,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Tombol tonal di zona tengah-bawah bila ada aksi
        if (!actionLabel.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            KonteninTonalButton(
                text = actionLabel,
                onClick = onActionClick,
                minHeight = KonteninTheme.dimensions.minTouchTarget
            )
        }
    }
}

