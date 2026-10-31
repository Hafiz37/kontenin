package com.example.kontenin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.kontenin.R
import com.example.kontenin.ui.theme.KonteninTheme

/**
 * Kolom Input Teks dengan Autocomplete Dropdown (FE-10).
 *
 * Acuan: PRD FR-2; DESIGN §5.2 (item 48 dp, radius-sm).
 * - Field tetap berupa teks bebas dan dapat diedit setelah memilih saran.
 * - Menampilkan maksimal 5 saran saat field fokus atau mengetik.
 * - Tanpa saran, dropdown disembunyikan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutocompleteField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Words,
        imeAction = ImeAction.Next
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textFieldModifier: Modifier = Modifier
) {
    val colors = KonteninTheme.colorScheme
    val isDropdownVisible = expanded && suggestions.isNotEmpty()
    val dropdownCd = stringResource(R.string.cd_autocomplete_dropdown)

    ExposedDropdownMenuBox(
        expanded = isDropdownVisible,
        onExpandedChange = onExpandedChange,
        modifier = modifier.fillMaxWidth()
    ) {
        KonteninTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            errorMessage = errorMessage,
            isError = isError,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                .then(textFieldModifier)
        )

        ExposedDropdownMenu(
            expanded = isDropdownVisible,
            onDismissRequest = { onExpandedChange(false) },
            shape = KonteninTheme.shapes.small, // radius-sm (8 dp)
            containerColor = colors.surface,
            modifier = Modifier
                .background(colors.surface)
                .semantics {
                    contentDescription = dropdownCd
                }
        ) {
            for (suggestion in suggestions) {
                val suggestionCd = stringResource(R.string.cd_autocomplete_suggestion, suggestion)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = suggestion,
                            style = KonteninTheme.typography.bodyMedium,
                            color = colors.onSurface
                        )
                    },
                    onClick = {
                        onSuggestionSelected(suggestion)
                        onExpandedChange(false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .semantics {
                            contentDescription = suggestionCd
                        }
                )
            }
        }
    }
}
