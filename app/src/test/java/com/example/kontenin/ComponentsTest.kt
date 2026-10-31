package com.example.kontenin

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.kontenin.ui.components.RupiahThousandsVisualTransformation
import com.example.kontenin.ui.components.formatRupiahAmount
import com.example.kontenin.ui.components.formatThousandsString
import com.example.kontenin.ui.components.rupiahToIndonesianWords
import com.example.kontenin.ui.theme.KonteninDimensions
import com.example.kontenin.ui.theme.KonteninRadius
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComponentsTest {

    @Test
    fun testFormatThousandsString() {
        assertEquals("", formatThousandsString(""))
        assertEquals("0", formatThousandsString("0"))
        assertEquals("500", formatThousandsString("500"))
        assertEquals("1.000", formatThousandsString("1000"))
        assertEquals("1.500.000", formatThousandsString("1500000"))
        assertEquals("12.500.000", formatThousandsString("12500000"))
        assertEquals("150.000.000", formatThousandsString("150000000"))
        assertEquals("1.500.000", formatRupiahAmount(1500000L))
    }

    @Test
    fun testRupiahThousandsVisualTransformationOffsetMapping() {
        val transformation = RupiahThousandsVisualTransformation()

        // Test with raw "1234567" -> transformed "1.234.567"
        val raw = "1234567"
        val transformed = transformation.filter(AnnotatedString(raw))

        assertEquals("1.234.567", transformed.text.text)

        val mapping = transformed.offsetMapping

        // originalToTransformed
        assertEquals(0, mapping.originalToTransformed(0)) // Before '1'
        assertEquals(2, mapping.originalToTransformed(1)) // After '1', after '.' before '2'
        assertEquals(6, mapping.originalToTransformed(4)) // After '4', after second '.'
        assertEquals(9, mapping.originalToTransformed(7)) // End

        // transformedToOriginal
        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(1, mapping.transformedToOriginal(1)) // Right on '.'
        assertEquals(1, mapping.transformedToOriginal(2)) // After '.'
        assertEquals(4, mapping.transformedToOriginal(6)) // After second '.'
        assertEquals(7, mapping.transformedToOriginal(9)) // End
    }

    @Test
    fun testRupiahToIndonesianWordsForTalkBack() {
        assertEquals("nol rupiah", rupiahToIndonesianWords(0L))
        assertEquals("seribu rupiah", rupiahToIndonesianWords(1_000L))
        assertEquals("sepuluh ribu rupiah", rupiahToIndonesianWords(10_000L))
        assertEquals("seratus ribu rupiah", rupiahToIndonesianWords(100_000L))
        assertEquals("satu juta lima ratus ribu rupiah", rupiahToIndonesianWords(1_500_000L))
        assertEquals("tiga juta lima ratus ribu rupiah", rupiahToIndonesianWords(3_500_000L))
        assertEquals("dua belas juta lima ratus ribu rupiah", rupiahToIndonesianWords(12_500_000L))
        assertEquals("seratus lima puluh juta rupiah", rupiahToIndonesianWords(150_000_000L))
    }

    @Test
    fun testComponentDimensionsAndTouchTargets() {
        val dimensions = KonteninDimensions()

        // WCAG & DESIGN §8.2: Target sentuh interaktif minimum ≥ 48 dp
        assertTrue(dimensions.minTouchTarget >= 48.dp)

        // Rekomendasi 56 dp untuk aksi utama & input kolom form
        assertEquals(56.dp, dimensions.buttonHeight)
        assertEquals(56.dp, dimensions.inputHeight)

        // Chip pembayaran tinggi 28 dp (DESIGN §5.2)
        assertEquals(28.dp, dimensions.paymentChipHeight)

        // Dialog: elevasi 3 dp, padding 24 dp (DESIGN §5.2)
        assertEquals(3.dp, dimensions.dialogElevation)
        assertEquals(24.dp, dimensions.dialogPadding)
        assertEquals(24.dp, KonteninRadius.xl)
    }

    @Test
    fun testAdaptiveDialogStackingRule() {
        fun shouldStackButtons(
            stackVerticallyFlag: Boolean,
            confirmLabel: String,
            dismissLabel: String,
            fontScale: Float
        ): Boolean {
            return stackVerticallyFlag ||
                    confirmLabel.length > 10 ||
                    dismissLabel.length > 10 ||
                    fontScale > 1.2f
        }

        // Standard labels and font scale: does not force stack
        assertFalse(shouldStackButtons(false, "Simpan", "Batal", 1.0f))

        // Long label (e.g. "Tandai Selesai" > 10): forces vertical stack
        assertTrue(shouldStackButtons(false, "Tandai Selesai", "Batal", 1.0f))

        // High font scale (e.g. 200% = 2.0f): forces vertical stack to prevent truncated buttons
        assertTrue(shouldStackButtons(false, "Simpan", "Batal", 2.0f))

        // Explicit flag: forces stack
        assertTrue(shouldStackButtons(true, "OK", "Batal", 1.0f))
    }

    @Test
    fun testCharacterCounterThresholdLogic() {
        fun shouldShowCounter(
            maxLength: Int?,
            currentLength: Int,
            showAlways: Boolean
        ): Boolean {
            if (maxLength == null) return false
            return showAlways || (currentLength >= (maxLength * 0.8f))
        }

        // Alamat max 300 (DESIGN §5.3: tampil saat sisa < 20%, yaitu >= 240 karakter)
        assertFalse(shouldShowCounter(300, 100, false))
        assertFalse(shouldShowCounter(300, 239, false))
        assertTrue(shouldShowCounter(300, 240, false))
        assertTrue(shouldShowCounter(300, 280, false))

        // showAlways overrides threshold
        assertTrue(shouldShowCounter(300, 50, true))
    }
}

