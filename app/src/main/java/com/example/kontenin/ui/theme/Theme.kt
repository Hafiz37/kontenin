package com.example.kontenin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Color Scheme (DESIGN §2.1)
val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    inversePrimary = InversePrimaryLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    scrim = ScrimColor,
)

// Dark Color Scheme (DESIGN §2.2)
val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    inversePrimary = InversePrimaryDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    scrim = ScrimColor,
)

// Extended Colors for Light Mode
val LightExtendedColors = KonteninExtendedColors(
    brand = Brand,
    onBrand = OnBrand,
    warningContainer = WarningContainerLight,
    onWarning = OnWarningLight,
    warningAccent = WarningAccentLight,
)

// Extended Colors for Dark Mode
val DarkExtendedColors = KonteninExtendedColors(
    brand = Brand,
    onBrand = OnBrand,
    warningContainer = WarningContainerDark,
    onWarning = OnWarningDark,
    warningAccent = WarningAccentDark,
)

val LocalKonteninExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun KonteninTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // DESIGN §2.3 / FE-03: Hindari dynamic color yang mengganti palet brand
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val dimensions = KonteninDimensions()
    val customTypography = CustomTypography

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalKonteninExtendedColors provides extendedColors,
        LocalKonteninDimensions provides dimensions,
        LocalKonteninTypography provides customTypography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = KonteninShapes,
            content = content
        )
    }
}

/**
 * Direct accessor object for Kontenin theme tokens and typography.
 */
object KonteninTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val extendedColors: KonteninExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalKonteninExtendedColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val customTypography: KonteninCustomTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalKonteninTypography.current

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val dimensions: KonteninDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalKonteninDimensions.current

    val spacing: KonteninSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalKonteninDimensions.current.spacing
}
