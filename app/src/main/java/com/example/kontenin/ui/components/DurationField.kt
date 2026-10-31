package com.example.kontenin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.kontenin.R

/**
 * Duration Field untuk input durasi dalam jam (FE-08).
 * Menerima desimal dengan koma atau titik (1,5 atau 1.5).
 * Menampilkan suffix " jam".
 *
 * Acuan: PRD FR-1.2 (durasi 0,5–24 jam, kelipatan 0,5; awal 4 jam).
 * DESIGN §5.2 (kolom durasi dengan satuan "jam").
 */
@Composable
fun DurationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    KonteninTextField(
        value = value,
        onValueChange = { input ->
            // Filter: hanya digit, koma, titik
            val filtered = input.filter { it.isDigit() || it == ',' || it == '.' }
            // Cegah multiple separator
            val hasSeparator = filtered.contains(',') || filtered.contains('.')
            val cleanedInput = if (hasSeparator) {
                // Ambil hanya satu separator pertama
                val firstSepIndex = filtered.indexOfFirst { it == ',' || it == '.' }
                if (firstSepIndex >= 0) {
                    val beforeSep = filtered.substring(0, firstSepIndex)
                    val afterSep = filtered.substring(firstSepIndex + 1).filter { it.isDigit() }
                    val separator = filtered[firstSepIndex]
                    "$beforeSep$separator$afterSep"
                } else {
                    filtered
                }
            } else {
                filtered
            }
            onValueChange(cleanedInput)
        },
        label = label,
        placeholder = "4",
        helperText = helperText,
        errorMessage = errorMessage,
        isError = isError,
        enabled = enabled,
        singleLine = true,
        trailingIcon = {
            Text(
                text = stringResource(R.string.field_duration_unit),
                style = com.example.kontenin.ui.theme.KonteninTheme.typography.bodyMedium,
                color = if (enabled) {
                    com.example.kontenin.ui.theme.KonteninTheme.colorScheme.onSurfaceVariant
                } else {
                    com.example.kontenin.ui.theme.KonteninTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}
