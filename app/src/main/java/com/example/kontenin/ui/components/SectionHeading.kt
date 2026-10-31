package com.example.kontenin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Section Heading Kontenin - DESIGN §3 & §5.2.
 * Tipografi 18 / 24 sp, SemiBold 600, semantik heading untuk aksesibilitas TalkBack.
 * Mendukung aksi opsional di sisi kanan dan subjudul.
 */
@Composable
fun SectionHeading(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                heading()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = customTypography.sectionHeading,
                color = colors.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = customTypography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }

        if (action != null) {
            action()
        }
    }
}

