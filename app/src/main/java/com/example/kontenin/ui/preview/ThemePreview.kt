package com.example.kontenin.ui.preview

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninTheme

@Composable
fun ThemePreviewScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val spacing = KonteninTheme.spacing
    val dimensions = KonteninTheme.dimensions
    val colors = KonteninTheme.colorScheme
    val extendedColors = KonteninTheme.extendedColors
    val customTypography = KonteninTheme.customTypography

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = dimensions.maxContentWidth)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = dimensions.screenMargin, vertical = spacing.space4),
                verticalArrangement = Arrangement.spacedBy(spacing.space5)
            ) {
                // Header
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                    Text(
                        text = stringResource(R.string.theme_preview_title),
                        style = customTypography.screenTitle,
                        color = colors.onSurface
                    )
                    Text(
                        text = stringResource(R.string.theme_preview_subtitle),
                        style = customTypography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 1. Typography Section
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = stringResource(R.string.theme_section_typography),
                        style = customTypography.sectionHeading,
                        color = colors.primary
                    )

                    // Angka Besar (Rekap / Piutang)
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = "Angka Besar (Tabular, Bold 28/36):",
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_large_number),
                            style = customTypography.largeNumber,
                            color = colors.onSurface
                        )
                    }

                    // Tanggal & Jam (Kartu & Detail)
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = "Tanggal & Jam (Tabular, SemiBold 20/28):",
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_date_time),
                            style = customTypography.dateTime,
                            color = colors.onSurface
                        )
                    }

                    // Judul Kartu
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = "Judul Kartu (SemiBold 16/24):",
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_couple),
                            style = customTypography.cardTitle,
                            color = colors.onSurface
                        )
                    }

                    // Nominal Tabular
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = "Nominal (Tabular, SemiBold 16/24):",
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_nominal),
                            style = customTypography.nominal,
                            color = colors.onSurface
                        )
                    }

                    // Body
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = "Body (Regular 16/24):",
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_body),
                            style = customTypography.body,
                            color = colors.onSurface
                        )
                    }

                    // Body Small & Caption
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.space1)) {
                        Text(
                            text = stringResource(R.string.theme_sample_body_small),
                            style = customTypography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.theme_sample_caption),
                            style = customTypography.caption,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 2. Buttons & Actions Section
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = stringResource(R.string.theme_section_actions),
                        style = customTypography.sectionHeading,
                        color = colors.primary
                    )

                    // Primary Button (Filled primary, white text, 56 dp)
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(KonteninRadius.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.theme_btn_primary),
                            style = customTypography.buttonLabel
                        )
                    }

                    // Tonal Button (Filled primary-container, on-primary-container text)
                    FilledTonalButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(KonteninRadius.md),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = colors.primaryContainer,
                            contentColor = colors.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.theme_btn_tonal),
                            style = customTypography.buttonLabel
                        )
                    }

                    // Outline Button
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(KonteninRadius.md),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.theme_btn_outline),
                            style = customTypography.buttonLabel
                        )
                    }

                    // Destructive Button
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(KonteninRadius.md),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.error,
                            contentColor = colors.onError
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.theme_btn_destructive),
                            style = customTypography.buttonLabel
                        )
                    }

                    // FAB Brand Container Showcase (DESIGN §2.1/2.2: Brand #7F997E, OnBrand #111A11)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FAB Tambah Job (Brand Token):",
                                style = customTypography.body,
                                color = colors.onSurface
                            )
                            Text(
                                text = "Brand #7F997E dengan isi gelap #111A11",
                                style = customTypography.caption,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(dimensions.fabSize)
                                .clip(RoundedCornerShape(KonteninRadius.lg))
                                .background(extendedColors.brand),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                style = customTypography.largeNumber,
                                color = extendedColors.onBrand,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(color = colors.outlineVariant)

                // 3. Status & Warning Tokens Section
                Column(verticalArrangement = Arrangement.spacedBy(spacing.space3)) {
                    Text(
                        text = stringResource(R.string.theme_section_status),
                        style = customTypography.sectionHeading,
                        color = colors.primary
                    )

                    // Warning Chip ("Belum lunas" / "Kurang Rp X")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacing.space2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .height(dimensions.paymentChipHeight)
                                .clip(RoundedCornerShape(KonteninRadius.sm))
                                .background(extendedColors.warningContainer)
                                .border(
                                    width = 1.dp,
                                    color = extendedColors.warningAccent,
                                    shape = RoundedCornerShape(KonteninRadius.sm)
                                )
                                .padding(horizontal = spacing.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.theme_chip_warning),
                                style = customTypography.caption,
                                color = extendedColors.onWarning,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(dimensions.paymentChipHeight)
                                .clip(RoundedCornerShape(KonteninRadius.sm))
                                .background(extendedColors.warningContainer)
                                .border(
                                    width = 1.dp,
                                    color = extendedColors.warningAccent,
                                    shape = RoundedCornerShape(KonteninRadius.sm)
                                )
                                .padding(horizontal = spacing.space3),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.theme_chip_warning_short),
                                style = customTypography.caption,
                                color = extendedColors.onWarning,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Error Container Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(KonteninRadius.sm))
                            .background(colors.errorContainer)
                            .padding(spacing.space4)
                    ) {
                        Text(
                            text = stringResource(R.string.theme_sample_error),
                            style = customTypography.body,
                            color = colors.onErrorContainer
                        )
                    }

                    // Inverse Snackbar Token Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(KonteninRadius.sm))
                            .background(colors.inverseSurface)
                            .padding(horizontal = spacing.space4, vertical = spacing.space3)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.theme_sample_snackbar),
                                style = customTypography.body,
                                color = colors.inverseOnSurface
                            )
                            TextButton(onClick = {}) {
                                Text(
                                    text = stringResource(R.string.theme_sample_snackbar_action),
                                    style = customTypography.buttonLabel,
                                    color = colors.inversePrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.space6))
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ThemePreviewLight() {
    KonteninTheme(darkTheme = false) {
        ThemePreviewScreen()
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ThemePreviewDark() {
    KonteninTheme(darkTheme = true) {
        ThemePreviewScreen()
    }
}

@Preview(name = "Font Scale 200%", fontScale = 2.0f, showBackground = true)
@Composable
fun ThemePreviewFontScale200() {
    KonteninTheme(darkTheme = false) {
        ThemePreviewScreen()
    }
}
