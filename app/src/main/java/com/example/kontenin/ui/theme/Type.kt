package com.example.kontenin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private const val TABULAR_NUMBERS_FEATURE = "tnum"

/**
 * Kontenin Semantic Typography roles as specified in DESIGN §3 (with D-8 Roboto system font).
 * All nominals, dates, and times use tabular figures (fontFeatureSettings = "tnum").
 * Minimum font size is 13 sp; minimum font weight is Regular 400.
 */
@Immutable
data class KonteninCustomTypography(
    /** Judul layar: 22 / 28 sp, SemiBold 600 (Top app bar) */
    val screenTitle: TextStyle,
    /** Angka besar: 28 / 36 sp, Bold 700, tabular (Total di kartu ringkasan Rekap, piutang) */
    val largeNumber: TextStyle,
    /** Heading seksi: 18 / 24 sp, SemiBold 600 (Pembayaran, Per Sumber Job, Piutang, Dialog) */
    val sectionHeading: TextStyle,
    /** Tanggal & jam: 20 / 28 sp, SemiBold 600, tabular (Kartu dan detail) */
    val dateTime: TextStyle,
    /** Judul kartu: 16 / 24 sp, SemiBold 600 (Nama pasangan: "Andi & Sari") */
    val cardTitle: TextStyle,
    /** Body: 16 / 24 sp, Regular 400 (Isi, nilai kolom, dialog, alamat, catatan) */
    val body: TextStyle,
    /** Nominal: 16 / 24 sp, SemiBold 600, tabular (Fee, dibayar, sisa tagihan) */
    val nominal: TextStyle,
    /** Body kecil: 14 / 20 sp, Regular 400 (Info sekunder: acara, sumber job di kartu, teks bantu) */
    val bodySmall: TextStyle,
    /** Caption: 13 / 18 sp, Medium 500 (Label kolom kecil, penghitung karakter, teks chip) */
    val caption: TextStyle,
    /** Label tombol: 16 / 24 sp, Medium 500 (Tombol, tab, label nav) */
    val buttonLabel: TextStyle,
)

val CustomTypography = KonteninCustomTypography(
    screenTitle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    largeNumber = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontFeatureSettings = TABULAR_NUMBERS_FEATURE,
    ),
    sectionHeading = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    dateTime = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        fontFeatureSettings = TABULAR_NUMBERS_FEATURE,
    ),
    cardTitle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    nominal = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFeatureSettings = TABULAR_NUMBERS_FEATURE,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    ),
    buttonLabel = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
)

val LocalKonteninTypography = staticCompositionLocalOf { CustomTypography }

/**
 * Standard Material 3 Typography mapping to Kontenin specifications.
 */
val Typography = Typography(
    displaySmall = CustomTypography.largeNumber,
    headlineMedium = CustomTypography.largeNumber,
    headlineSmall = CustomTypography.dateTime,
    titleLarge = CustomTypography.screenTitle,
    titleMedium = CustomTypography.sectionHeading,
    titleSmall = CustomTypography.cardTitle,
    bodyLarge = CustomTypography.body,
    bodyMedium = CustomTypography.bodySmall,
    bodySmall = CustomTypography.caption,
    labelLarge = CustomTypography.buttonLabel,
    labelMedium = CustomTypography.caption,
    labelSmall = CustomTypography.caption,
)
