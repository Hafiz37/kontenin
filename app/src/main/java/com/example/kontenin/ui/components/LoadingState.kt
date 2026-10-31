package com.example.kontenin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Indikator Loading Standar Kontenin - DESIGN §7.2.
 */
@Composable
fun KonteninLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    val loadingDescription = stringResource(R.string.cd_loading)
    CircularProgressIndicator(
        modifier = modifier
            .size(size.dp)
            .semantics { contentDescription = loadingDescription },
        color = KonteninTheme.colorScheme.primary,
        strokeWidth = 2.5.dp
    )
}

/**
 * Kartu Kerangka (Skeleton) Statis - DESIGN §7.2.
 * Menampilkan kartu placeholder tanpa animasi berkedip/shimmer untuk menjaga ketenangan visual.
 */
@Composable
fun SkeletonJobCard(
    modifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val cardShape = RoundedCornerShape(KonteninRadius.md)
    val placeholderColor = colors.surfaceVariant
    val blockShape = RoundedCornerShape(KonteninRadius.sm)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.surface)
            .border(
                width = 1.dp,
                color = colors.outlineVariant,
                shape = cardShape
            )
            .padding(KonteninTheme.dimensions.cardPadding)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Baris Tanggal & Jam skeleton
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(18.dp)
                    .clip(blockShape)
                    .background(placeholderColor)
            )

            // Nama Pasangan skeleton
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(22.dp)
                    .clip(blockShape)
                    .background(placeholderColor)
            )

            // Info sekunder skeleton (Acara · Sumber)
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(14.dp)
                    .clip(blockShape)
                    .background(placeholderColor)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Baris Fee & Status chip skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(18.dp)
                        .clip(blockShape)
                        .background(placeholderColor)
                )

                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(KonteninTheme.dimensions.paymentChipHeight)
                        .clip(blockShape)
                        .background(placeholderColor)
                )
            }
        }
    }
}

/**
 * Daftar Kartu Kerangka Statis (3 kartu kerangka statis, tanpa animasi berkedip - DESIGN §7.2).
 */
@Composable
fun SkeletonJobList(
    modifier: Modifier = Modifier,
    count: Int = 3
) {
    val loadingDescription = stringResource(R.string.cd_loading)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = loadingDescription },
        verticalArrangement = Arrangement.spacedBy(KonteninTheme.dimensions.cardSpacing)
    ) {
        repeat(count) {
            SkeletonJobCard()
        }
    }
}

/**
 * Layar Loading Penuh / Kontainer dengan Indikator Memutar dan Pesan Opsional (DESIGN §7.2).
 */
@Composable
fun LoadingStateScreen(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val customTypography = KonteninTheme.customTypography
    val colors = KonteninTheme.colorScheme

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KonteninLoadingIndicator(size = 40)
            if (!message.isNullOrBlank()) {
                Text(
                    text = message,
                    style = customTypography.body,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

