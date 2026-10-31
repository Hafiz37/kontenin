package com.example.kontenin.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale (basis 4 dp) as specified in DESIGN §4.1.
 */
@Immutable
data class KonteninSpacing(
    /** 4 dp: Jarak ikon-teks rapat */
    val space1: Dp = 4.dp,
    /** 8 dp: Jarak minimum antar target sentuh; jarak chip */
    val space2: Dp = 8.dp,
    /** 12 dp: Padding dalam chip dan baris */
    val space3: Dp = 12.dp,
    /** 16 dp: Margin layar, padding kartu, jarak antar kolom form */
    val space4: Dp = 16.dp,
    /** 24 dp: Jarak antar seksi */
    val space5: Dp = 24.dp,
    /** 32 dp: Jarak besar (di atas tombol aksi akhir) */
    val space6: Dp = 32.dp,
)

/**
 * Dimensions and layout constraints from DESIGN §4.2 and §5.
 */
@Immutable
data class KonteninDimensions(
    val spacing: KonteninSpacing = KonteninSpacing(),
    /** 16 dp: Margin layar HP potret (< 600 dp) */
    val screenMargin: Dp = 16.dp,
    /** 600 dp: Batas lebar konten maksimal (DESIGN §4.2, D-9) */
    val maxContentWidth: Dp = 600.dp,
    /** 16 dp: Grid gutter */
    val gutter: Dp = 16.dp,
    /** 12 dp: Jarak antar kartu dalam daftar */
    val cardSpacing: Dp = 12.dp,
    /** 16 dp: Padding kartu */
    val cardPadding: Dp = 16.dp,
    /** 48 dp: Target sentuh interaktif minimum (WCAG / DESIGN §5.1) */
    val minTouchTarget: Dp = 48.dp,
    /** 56 dp: Tinggi tombol utama dan form input */
    val buttonHeight: Dp = 56.dp,
    val inputHeight: Dp = 56.dp,
    /** 64 dp: Tinggi top app bar */
    val topAppBarHeight: Dp = 64.dp,
    /** 80 dp: Tinggi bottom navigation bar dan bottom action bar */
    val bottomNavHeight: Dp = 80.dp,
    val bottomActionBarHeight: Dp = 80.dp,
    /** 28 dp: Tinggi chip status pembayaran */
    val paymentChipHeight: Dp = 28.dp,
    /** 56 dp: Ukuran FAB */
    val fabSize: Dp = 56.dp,
    /** 2 dp: FAB elevation */
    val fabElevation: Dp = 2.dp,
    /** 3 dp: Dialog elevation */
    val dialogElevation: Dp = 3.dp,
    /** 24 dp: Padding isi dialog */
    val dialogPadding: Dp = 24.dp,
)

val LocalKonteninDimensions = staticCompositionLocalOf { KonteninDimensions() }
