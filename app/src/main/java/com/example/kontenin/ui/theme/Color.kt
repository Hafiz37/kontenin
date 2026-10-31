package com.example.kontenin.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Brand colors (DESIGN §2)
val Brand = Color(0xFF7F997E)
val OnBrand = Color(0xFF111A11)

// Light Mode Tokens (DESIGN §2.1)
val PrimaryLight = Color(0xFF2C452C)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE3EBE2)
val OnPrimaryContainerLight = Color(0xFF2C452C)
val BackgroundLight = Color(0xFFF6F7F5)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFECEFEA)
val OnSurfaceLight = Color(0xFF1A1D1A)
val OnSurfaceVariantLight = Color(0xFF444B44)
val OutlineLight = Color(0xFF6B746A)
val OutlineVariantLight = Color(0xFFD5DAD3)
val WarningContainerLight = Color(0xFFFFF0CC)
val OnWarningLight = Color(0xFF5C3A00)
val WarningAccentLight = Color(0xFF9A5B00)
val ErrorLight = Color(0xFFB3261E)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFDE7E5)
val OnErrorContainerLight = Color(0xFF7A1712)
val InverseSurfaceLight = Color(0xFF2A2F2A)
val InverseOnSurfaceLight = Color(0xFFF1F4F0)
val InversePrimaryLight = Color(0xFFB9D6B8)
val ScrimColor = Color(0x52000000) // #000000 @ 32%

// Dark Mode Tokens (DESIGN §2.2)
val PrimaryDark = Color(0xFFA9C4A8)
val OnPrimaryDark = Color(0xFF0F1A0F)
val PrimaryContainerDark = Color(0xFF2A3B29)
val OnPrimaryContainerDark = Color(0xFFD5E6D3)
val BackgroundDark = Color(0xFF101310)
val SurfaceDark = Color(0xFF181C18)
val SurfaceVariantDark = Color(0xFF232823)
val OnSurfaceDark = Color(0xFFE4E8E3)
val OnSurfaceVariantDark = Color(0xFFB9C1B8)
val OutlineDark = Color(0xFF8A938A)
val OutlineVariantDark = Color(0xFF3A423A)
val WarningContainerDark = Color(0xFF4A3600)
val OnWarningDark = Color(0xFFFFDEA3)
val WarningAccentDark = Color(0xFFF2B84B)
val ErrorDark = Color(0xFFF2B8B5)
val OnErrorDark = Color(0xFF601410)
val ErrorContainerDark = Color(0xFF5E1A16)
val OnErrorContainerDark = Color(0xFFFFD9D6)
val InverseSurfaceDark = Color(0xFFE4E8E3)
val InverseOnSurfaceDark = Color(0xFF1A1D1A)
val InversePrimaryDark = Color(0xFF2C452C)

/**
 * Extended color tokens for Kontenin that are outside standard Material 3 ColorScheme:
 * Brand and Warning tokens (DESIGN §2.1 & §2.2).
 */
@Immutable
data class KonteninExtendedColors(
    val brand: Color,
    val onBrand: Color,
    val warningContainer: Color,
    val onWarning: Color,
    val warningAccent: Color,
)
