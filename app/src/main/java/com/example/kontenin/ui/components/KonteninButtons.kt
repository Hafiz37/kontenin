package com.example.kontenin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Common Button Content with loading indicator and optional leading icon.
 * Ensures texts can wrap or grow gracefully without truncation up to 200% font scale.
 */
@Composable
private fun KonteninButtonContent(
    text: String,
    loading: Boolean,
    indicatorColor: Color,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = indicatorColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = text,
            style = KonteninTheme.customTypography.buttonLabel,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tombol Utama (Primary) - DESIGN §5.2.
 * Isi `primary`, teks `on-primary` (putih di light mode).
 * Tinggi default/min 56 dp, target sentuh ≥ 48 dp.
 * Digunakan untuk aksi utama: Simpan Job, Tambah, dialog aksi utama.
 */
@Composable
fun KonteninPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    minHeight: Dp = KonteninTheme.dimensions.buttonHeight, // 56 dp
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val isInteractive = enabled && !loading

    Button(
        onClick = onClick,
        modifier = modifier
            .semantics { role = Role.Button }
            .heightIn(min = minHeight)
            .defaultMinSize(minHeight = minHeight),
        enabled = isInteractive,
        shape = KonteninTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
            disabledContainerColor = colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        KonteninButtonContent(
            text = text,
            loading = loading,
            indicatorColor = colors.onPrimary,
            leadingIcon = leadingIcon
        )
    }
}

/**
 * Tombol Tonal - DESIGN §5.2.
 * Isi `primary-container`, teks `on-primary-container`.
 * Tinggi min 48 dp (bisa 56 dp).
 * Digunakan untuk: Lunas, Tandai Lunas, Ubah Pembayaran, Buka Maps, Aksi Empty State.
 */
@Composable
fun KonteninTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    minHeight: Dp = KonteninTheme.dimensions.minTouchTarget, // 48 dp
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val isInteractive = enabled && !loading

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier
            .semantics { role = Role.Button }
            .heightIn(min = minHeight)
            .defaultMinSize(minHeight = minHeight),
        enabled = isInteractive,
        shape = KonteninTheme.shapes.medium,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = colors.primaryContainer,
            contentColor = colors.onPrimaryContainer,
            disabledContainerColor = colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
    ) {
        KonteninButtonContent(
            text = text,
            loading = loading,
            indicatorColor = colors.onPrimaryContainer,
            leadingIcon = leadingIcon
        )
    }
}

/**
 * Tombol Outline - DESIGN §5.2.
 * Garis `outline`, latar transparan, teks `primary`.
 * Tinggi min 48 dp.
 * Digunakan untuk: Duplikat, Kembali, Ubah.
 */
@Composable
fun KonteninOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    minHeight: Dp = KonteninTheme.dimensions.minTouchTarget, // 48 dp
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val isInteractive = enabled && !loading

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .semantics { role = Role.Button }
            .heightIn(min = minHeight)
            .defaultMinSize(minHeight = minHeight),
        enabled = isInteractive,
        shape = KonteninTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = colors.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isInteractive) colors.outline else colors.outline.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
    ) {
        KonteninButtonContent(
            text = text,
            loading = loading,
            indicatorColor = colors.primary,
            leadingIcon = leadingIcon
        )
    }
}

/**
 * Tombol Destruktif - DESIGN §5.2.
 * Di dialog: isi `error` + teks `on-error` (outlined = false).
 * Di halaman: outline `error` + teks `error` (outlined = true).
 */
@Composable
fun KonteninDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
    minHeight: Dp = if (outlined) KonteninTheme.dimensions.minTouchTarget else KonteninTheme.dimensions.buttonHeight,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme
    val isInteractive = enabled && !loading

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
                .semantics { role = Role.Button }
                .heightIn(min = minHeight)
                .defaultMinSize(minHeight = minHeight),
            enabled = isInteractive,
            shape = KonteninTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = colors.error,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (isInteractive) colors.error else colors.error.copy(alpha = 0.38f)
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        ) {
            KonteninButtonContent(
                text = text,
                loading = loading,
                indicatorColor = colors.error,
                leadingIcon = leadingIcon
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier
                .semantics { role = Role.Button }
                .heightIn(min = minHeight)
                .defaultMinSize(minHeight = minHeight),
            enabled = isInteractive,
            shape = KonteninTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.error,
                contentColor = colors.onError,
                disabledContainerColor = colors.onSurface.copy(alpha = 0.12f),
                disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            KonteninButtonContent(
                text = text,
                loading = loading,
                indicatorColor = colors.onError,
                leadingIcon = leadingIcon
            )
        }
    }
}

/**
 * Tombol Teks - DESIGN §5.2.
 * Latar transparan, teks `primary`, target sentuh minimum 48 dp.
 * Digunakan untuk: Batal, tautan.
 */
@Composable
fun KonteninTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = KonteninTheme.dimensions.minTouchTarget, // 48 dp
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colors = KonteninTheme.colorScheme

    TextButton(
        onClick = onClick,
        modifier = modifier
            .semantics { role = Role.Button }
            .heightIn(min = minHeight)
            .defaultMinSize(minHeight = minHeight),
        enabled = enabled,
        shape = KonteninTheme.shapes.medium,
        colors = ButtonDefaults.textButtonColors(
            contentColor = colors.primary,
            disabledContentColor = colors.onSurface.copy(alpha = 0.38f)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        KonteninButtonContent(
            text = text,
            loading = false,
            indicatorColor = colors.primary,
            leadingIcon = leadingIcon
        )
    }
}

