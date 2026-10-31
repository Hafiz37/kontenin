package com.example.kontenin

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kontenin.ui.theme.BackgroundDark
import com.example.kontenin.ui.theme.BackgroundLight
import com.example.kontenin.ui.theme.Brand
import com.example.kontenin.ui.theme.CustomTypography
import com.example.kontenin.ui.theme.DarkColorScheme
import com.example.kontenin.ui.theme.DarkExtendedColors
import com.example.kontenin.ui.theme.ErrorDark
import com.example.kontenin.ui.theme.ErrorLight
import com.example.kontenin.ui.theme.InverseOnSurfaceDark
import com.example.kontenin.ui.theme.InverseOnSurfaceLight
import com.example.kontenin.ui.theme.InversePrimaryDark
import com.example.kontenin.ui.theme.InversePrimaryLight
import com.example.kontenin.ui.theme.InverseSurfaceDark
import com.example.kontenin.ui.theme.InverseSurfaceLight
import com.example.kontenin.ui.theme.KonteninDimensions
import com.example.kontenin.ui.theme.KonteninRadius
import com.example.kontenin.ui.theme.KonteninSpacing
import com.example.kontenin.ui.theme.LightColorScheme
import com.example.kontenin.ui.theme.LightExtendedColors
import com.example.kontenin.ui.theme.OnBrand
import com.example.kontenin.ui.theme.OnErrorDark
import com.example.kontenin.ui.theme.OnErrorLight
import com.example.kontenin.ui.theme.OnPrimaryContainerDark
import com.example.kontenin.ui.theme.OnPrimaryContainerLight
import com.example.kontenin.ui.theme.OnPrimaryDark
import com.example.kontenin.ui.theme.OnPrimaryLight
import com.example.kontenin.ui.theme.OnSurfaceDark
import com.example.kontenin.ui.theme.OnSurfaceLight
import com.example.kontenin.ui.theme.OnSurfaceVariantDark
import com.example.kontenin.ui.theme.OnSurfaceVariantLight
import com.example.kontenin.ui.theme.OnWarningDark
import com.example.kontenin.ui.theme.OnWarningLight
import com.example.kontenin.ui.theme.OutlineDark
import com.example.kontenin.ui.theme.OutlineLight
import com.example.kontenin.ui.theme.OutlineVariantDark
import com.example.kontenin.ui.theme.OutlineVariantLight
import com.example.kontenin.ui.theme.PrimaryContainerDark
import com.example.kontenin.ui.theme.PrimaryContainerLight
import com.example.kontenin.ui.theme.PrimaryDark
import com.example.kontenin.ui.theme.PrimaryLight
import com.example.kontenin.ui.theme.SurfaceDark
import com.example.kontenin.ui.theme.SurfaceLight
import com.example.kontenin.ui.theme.SurfaceVariantDark
import com.example.kontenin.ui.theme.SurfaceVariantLight
import com.example.kontenin.ui.theme.WarningAccentDark
import com.example.kontenin.ui.theme.WarningAccentLight
import com.example.kontenin.ui.theme.WarningContainerDark
import com.example.kontenin.ui.theme.WarningContainerLight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ThemeTokensTest {

    // Helper: calculate WCAG 2.x relative luminance
    private fun relativeLuminance(color: Color): Double {
        fun channel(c: Float): Double {
            val d = c.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(color.red)
        val g = channel(color.green)
        val b = channel(color.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    // Helper: calculate WCAG contrast ratio between two colors
    private fun contrastRatio(c1: Color, c2: Color): Double {
        val l1 = relativeLuminance(c1)
        val l2 = relativeLuminance(c2)
        val lighter = max(l1, l2)
        val darker = min(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testLightModeColorTokensMatchDesign() {
        assertEquals(Color(0xFF2C452C), PrimaryLight)
        assertEquals(Color(0xFFFFFFFF), OnPrimaryLight)
        assertEquals(Color(0xFFE3EBE2), PrimaryContainerLight)
        assertEquals(Color(0xFF2C452C), OnPrimaryContainerLight)
        assertEquals(Color(0xFFF6F7F5), BackgroundLight)
        assertEquals(Color(0xFFFFFFFF), SurfaceLight)
        assertEquals(Color(0xFFECEFEA), SurfaceVariantLight)
        assertEquals(Color(0xFF1A1D1A), OnSurfaceLight)
        assertEquals(Color(0xFF444B44), OnSurfaceVariantLight)
        assertEquals(Color(0xFF6B746A), OutlineLight)
        assertEquals(Color(0xFFD5DAD3), OutlineVariantLight)
        assertEquals(Color(0xFFFFF0CC), WarningContainerLight)
        assertEquals(Color(0xFF5C3A00), OnWarningLight)
        assertEquals(Color(0xFF9A5B00), WarningAccentLight)
        assertEquals(Color(0xFFB3261E), ErrorLight)
        assertEquals(Color(0xFFFFFFFF), OnErrorLight)
        assertEquals(Color(0xFF2A2F2A), InverseSurfaceLight)
        assertEquals(Color(0xFFF1F4F0), InverseOnSurfaceLight)
        assertEquals(Color(0xFFB9D6B8), InversePrimaryLight)
        assertEquals(Color(0xFF7F997E), Brand)
        assertEquals(Color(0xFF111A11), OnBrand)

        // Verify LightColorScheme integration
        assertEquals(PrimaryLight, LightColorScheme.primary)
        assertEquals(OnPrimaryLight, LightColorScheme.onPrimary)
        assertEquals(BackgroundLight, LightColorScheme.background)
        assertEquals(SurfaceLight, LightColorScheme.surface)
        assertEquals(OnSurfaceLight, LightColorScheme.onSurface)
    }

    @Test
    fun testDarkModeColorTokensMatchDesign() {
        assertEquals(Color(0xFFA9C4A8), PrimaryDark)
        assertEquals(Color(0xFF0F1A0F), OnPrimaryDark)
        assertEquals(Color(0xFF2A3B29), PrimaryContainerDark)
        assertEquals(Color(0xFFD5E6D3), OnPrimaryContainerDark)
        assertEquals(Color(0xFF101310), BackgroundDark)
        assertEquals(Color(0xFF181C18), SurfaceDark)
        assertEquals(Color(0xFF232823), SurfaceVariantDark)
        assertEquals(Color(0xFFE4E8E3), OnSurfaceDark)
        assertEquals(Color(0xFFB9C1B8), OnSurfaceVariantDark)
        assertEquals(Color(0xFF8A938A), OutlineDark)
        assertEquals(Color(0xFF3A423A), OutlineVariantDark)
        assertEquals(Color(0xFF4A3600), WarningContainerDark)
        assertEquals(Color(0xFFFFDEA3), OnWarningDark)
        assertEquals(Color(0xFFF2B84B), WarningAccentDark)
        assertEquals(Color(0xFFF2B8B5), ErrorDark)
        assertEquals(Color(0xFF601410), OnErrorDark)
        assertEquals(Color(0xFFE4E8E3), InverseSurfaceDark)
        assertEquals(Color(0xFF1A1D1A), InverseOnSurfaceDark)
        assertEquals(Color(0xFF2C452C), InversePrimaryDark)

        // Verify DarkColorScheme integration
        assertEquals(PrimaryDark, DarkColorScheme.primary)
        assertEquals(OnPrimaryDark, DarkColorScheme.onPrimary)
        assertEquals(BackgroundDark, DarkColorScheme.background)
        assertEquals(SurfaceDark, DarkColorScheme.surface)
        assertEquals(OnSurfaceDark, DarkColorScheme.onSurface)
    }

    @Test
    fun testExtendedColorsHoldBrandAndWarning() {
        assertEquals(Brand, LightExtendedColors.brand)
        assertEquals(OnBrand, LightExtendedColors.onBrand)
        assertEquals(WarningContainerLight, LightExtendedColors.warningContainer)
        assertEquals(OnWarningLight, LightExtendedColors.onWarning)
        assertEquals(WarningAccentLight, LightExtendedColors.warningAccent)

        assertEquals(Brand, DarkExtendedColors.brand)
        assertEquals(OnBrand, DarkExtendedColors.onBrand)
        assertEquals(WarningContainerDark, DarkExtendedColors.warningContainer)
        assertEquals(OnWarningDark, DarkExtendedColors.onWarning)
        assertEquals(WarningAccentDark, DarkExtendedColors.warningAccent)
    }

    @Test
    fun testContrastRatiosComplyWithDesignWcag() {
        // Light mode checks
        assertTrue("Primary Light vs OnPrimary Light >= 7.0 (AAA)", contrastRatio(PrimaryLight, OnPrimaryLight) >= 7.0)
        assertTrue("OnSurface Light vs Surface Light >= 7.0 (AAA)", contrastRatio(OnSurfaceLight, SurfaceLight) >= 7.0)
        assertTrue("Brand vs OnBrand >= 4.5 (AA)", contrastRatio(Brand, OnBrand) >= 4.5)
        assertTrue("OnWarning Light vs WarningContainer Light >= 4.5 (AA)", contrastRatio(OnWarningLight, WarningContainerLight) >= 4.5)
        assertTrue("InverseOnSurface vs InverseSurface Light >= 7.0", contrastRatio(InverseOnSurfaceLight, InverseSurfaceLight) >= 7.0)

        // Dark mode checks
        assertTrue("Primary Dark vs OnPrimary Dark >= 7.0", contrastRatio(PrimaryDark, OnPrimaryDark) >= 7.0)
        assertTrue("OnSurface Dark vs Surface Dark >= 7.0", contrastRatio(OnSurfaceDark, SurfaceDark) >= 7.0)
        assertTrue("OnWarning Dark vs WarningContainer Dark >= 4.5", contrastRatio(OnWarningDark, WarningContainerDark) >= 4.5)
        assertTrue("InverseOnSurface vs InverseSurface Dark >= 7.0", contrastRatio(InverseOnSurfaceDark, InverseSurfaceDark) >= 7.0)
    }

    @Test
    fun testDimensionsAndSpacingMatchDesign() {
        val spacing = KonteninSpacing()
        assertEquals(4.dp, spacing.space1)
        assertEquals(8.dp, spacing.space2)
        assertEquals(12.dp, spacing.space3)
        assertEquals(16.dp, spacing.space4)
        assertEquals(24.dp, spacing.space5)
        assertEquals(32.dp, spacing.space6)

        assertEquals(8.dp, KonteninRadius.sm)
        assertEquals(12.dp, KonteninRadius.md)
        assertEquals(16.dp, KonteninRadius.lg)
        assertEquals(24.dp, KonteninRadius.xl)
        assertEquals(999.dp, KonteninRadius.full)

        val dimensions = KonteninDimensions()
        assertEquals(16.dp, dimensions.screenMargin)
        assertEquals(600.dp, dimensions.maxContentWidth)
        assertEquals(48.dp, dimensions.minTouchTarget)
        assertEquals(56.dp, dimensions.buttonHeight)
        assertEquals(56.dp, dimensions.fabSize)
    }

    @Test
    fun testTypographyTokensMatchDesignAndHaveTabularFigures() {
        // Minimum font size must be at least 13 sp
        assertTrue("Caption >= 13 sp", CustomTypography.caption.fontSize >= 13.sp)
        assertTrue("BodySmall >= 13 sp", CustomTypography.bodySmall.fontSize >= 13.sp)
        assertTrue("Body >= 13 sp", CustomTypography.body.fontSize >= 13.sp)

        // Tabular figures check for numbers, dates, and times (DESIGN §3)
        assertEquals("tnum", CustomTypography.largeNumber.fontFeatureSettings)
        assertEquals("tnum", CustomTypography.dateTime.fontFeatureSettings)
        assertEquals("tnum", CustomTypography.nominal.fontFeatureSettings)

        // Exact sizing from DESIGN §3
        assertEquals(22.sp, CustomTypography.screenTitle.fontSize)
        assertEquals(28.sp, CustomTypography.screenTitle.lineHeight)
        assertEquals(28.sp, CustomTypography.largeNumber.fontSize)
        assertEquals(36.sp, CustomTypography.largeNumber.lineHeight)
        assertEquals(18.sp, CustomTypography.sectionHeading.fontSize)
        assertEquals(24.sp, CustomTypography.sectionHeading.lineHeight)
        assertEquals(20.sp, CustomTypography.dateTime.fontSize)
        assertEquals(28.sp, CustomTypography.dateTime.lineHeight)
        assertEquals(16.sp, CustomTypography.cardTitle.fontSize)
        assertEquals(24.sp, CustomTypography.cardTitle.lineHeight)
        assertEquals(16.sp, CustomTypography.body.fontSize)
        assertEquals(24.sp, CustomTypography.body.lineHeight)
        assertEquals(16.sp, CustomTypography.nominal.fontSize)
        assertEquals(24.sp, CustomTypography.nominal.lineHeight)
        assertEquals(14.sp, CustomTypography.bodySmall.fontSize)
        assertEquals(20.sp, CustomTypography.bodySmall.lineHeight)
        assertEquals(13.sp, CustomTypography.caption.fontSize)
        assertEquals(18.sp, CustomTypography.caption.lineHeight)
        assertEquals(16.sp, CustomTypography.buttonLabel.fontSize)
        assertEquals(24.sp, CustomTypography.buttonLabel.lineHeight)
    }
}
