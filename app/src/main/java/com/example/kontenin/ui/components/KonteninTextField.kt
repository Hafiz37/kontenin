package com.example.kontenin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Kolom Input Teks Kontenin - DESIGN §5.2.
 * Tinggi minimum 56 dp, label di atas, garis outline 1 dp (fokus 2 dp primary).
 * Mendukung status default, fokus, disabled, dan error (ikon + teks bantu error).
 * Mendukung penghitung karakter (DESIGN §5.3) untuk Alamat (300) dan Catatan (1000).
 */
@Composable
fun KonteninTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    maxLength: Int? = null,
    showCounterAlways: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = KonteninTheme.colorScheme
    val customTypography = KonteninTheme.customTypography

    // Tampilkan counter bila showCounterAlways = true, atau bila tersisa < 20% kapasitas (DESIGN §5.3)
    val shouldShowCounter = maxLength != null && (
        showCounterAlways || (value.length >= (maxLength * 0.8f))
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = KonteninTheme.dimensions.inputHeight)
                .semantics {
                    if (isError && !errorMessage.isNullOrBlank()) {
                        error(errorMessage)
                    }
                },
            enabled = enabled,
            readOnly = readOnly,
            textStyle = customTypography.body.copy(color = if (enabled) colors.onSurface else colors.onSurface.copy(alpha = 0.38f)),
            label = {
                Text(
                    text = label,
                    style = customTypography.bodySmall
                )
            },
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = customTypography.body,
                        color = colors.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (isError) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.cd_error),
                        tint = colors.error
                    )
                } else if (trailingIcon != null) {
                    trailingIcon()
                }
            },
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            shape = KonteninTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colors.onSurface,
                unfocusedTextColor = colors.onSurface,
                disabledTextColor = colors.onSurface.copy(alpha = 0.38f),
                errorTextColor = colors.onSurface,
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.outline,
                disabledBorderColor = colors.outline.copy(alpha = 0.38f),
                errorBorderColor = colors.error,
                focusedLabelColor = colors.primary,
                unfocusedLabelColor = colors.onSurfaceVariant,
                disabledLabelColor = colors.onSurfaceVariant.copy(alpha = 0.38f),
                errorLabelColor = colors.error,
                focusedLeadingIconColor = colors.primary,
                unfocusedLeadingIconColor = colors.onSurfaceVariant,
                disabledLeadingIconColor = colors.onSurfaceVariant.copy(alpha = 0.38f),
                errorLeadingIconColor = colors.error,
                focusedTrailingIconColor = colors.primary,
                unfocusedTrailingIconColor = colors.onSurfaceVariant,
                disabledTrailingIconColor = colors.onSurfaceVariant.copy(alpha = 0.38f),
                errorTrailingIconColor = colors.error,
                disabledContainerColor = colors.surfaceVariant.copy(alpha = 0.4f)
            )
        )

        // Supporting row: Pesan error/bantu di kiri, penghitung karakter di kanan (DESIGN §5.2, §5.3)
        if (isError || helperText != null || shouldShowCounter) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Teks bantu atau teks error
                val textToDisplay = if (isError && !errorMessage.isNullOrBlank()) {
                    errorMessage
                } else {
                    helperText
                }

                if (!textToDisplay.isNullOrBlank()) {
                    Text(
                        text = textToDisplay,
                        style = customTypography.caption,
                        color = if (isError) colors.error else colors.onSurfaceVariant,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                } else {
                    // Spacer filler if only counter is visible
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                }

                // Penghitung karakter (DESIGN §5.3)
                if (shouldShowCounter) {
                    Text(
                        text = stringResource(R.string.char_counter_format, value.length, maxLength ?: 0),
                        style = customTypography.caption,
                        color = if (value.length >= maxLength) colors.error else colors.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
