package com.example.kontenin.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Kontenin Corner Radius tokens (DESIGN §4.3).
 */
object KonteninRadius {
    /** 8 dp: Chip, snackbar, item dropdown autocomplete */
    val sm = 8.dp

    /** 12 dp: Tombol, kolom input, kartu, segmented button */
    val md = 12.dp

    /** 16 dp: FAB, kartu ringkasan Rekap */
    val lg = 16.dp

    /** 24 dp: Dialog */
    val xl = 24.dp

    /** 999 dp: Indikator nav, indikator tab */
    val full = 999.dp
}

val KonteninShapes = Shapes(
    small = RoundedCornerShape(KonteninRadius.sm),
    medium = RoundedCornerShape(KonteninRadius.md),
    large = RoundedCornerShape(KonteninRadius.lg),
    extraLarge = RoundedCornerShape(KonteninRadius.xl)
)
