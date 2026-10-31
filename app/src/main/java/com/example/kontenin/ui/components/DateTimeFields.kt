package com.example.kontenin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.example.kontenin.R
import com.example.kontenin.util.KonteninDatePickerConverter
import com.example.kontenin.util.formatDate
import com.example.kontenin.util.formatTime
import java.time.LocalDate

/**
 * Date Field dengan Material 3 DatePicker (FE-08).
 * Read-only field yang membuka DatePickerDialog saat diketuk.
 *
 * Acuan: DESIGN §5.2; PRD FR-1.2 (tanggal masa lalu boleh diisi).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
) {
    val displayText = selectedDate?.let { formatDate(it) } ?: ""
    val placeholderText = stringResource(R.string.field_date_placeholder)

    Box(modifier = modifier) {
        KonteninTextField(
            value = displayText,
            onValueChange = { /* Read-only */ },
            label = label,
            placeholder = placeholderText,
            helperText = helperText,
            errorMessage = errorMessage,
            isError = isError,
            enabled = enabled,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.cd_select_date)
                )
            },
            modifier = Modifier.clickable(
                enabled = enabled,
                onClick = { /* Handled by parent */ },
                role = Role.Button
            )
        )
    }
}

/**
 * Time Field dengan Material 3 TimePicker (FE-08).
 * Read-only field yang membuka TimePickerDialog saat diketuk.
 *
 * Acuan: DESIGN §5.2; PRD FR-1.2 (jam mulai wajib).
 */
@Composable
fun TimeField(
    selectedTimeMinutes: Int?,
    onTimeSelected: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    helperText: String? = null,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    enabled: Boolean = true,
) {
    val displayText = selectedTimeMinutes?.let { formatTime(it) } ?: ""
    val placeholderText = stringResource(R.string.field_time_placeholder)

    Box(modifier = modifier) {
        KonteninTextField(
            value = displayText,
            onValueChange = { /* Read-only */ },
            label = label,
            placeholder = placeholderText,
            helperText = helperText,
            errorMessage = errorMessage,
            isError = isError,
            enabled = enabled,
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = stringResource(R.string.cd_select_time)
                )
            },
            modifier = Modifier.clickable(
                enabled = enabled,
                onClick = { /* Handled by parent */ },
                role = Role.Button
            )
        )
    }
}

/**
 * Material 3 DatePickerDialog untuk memilih tanggal (FE-08).
 * Konversi UTC → LocalDate melalui DatePickerConverter untuk menghindari shift tanggal.
 *
 * Acuan: PRD EC-22; DESIGN §5.2.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    initialDate: LocalDate? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.let {
            KonteninDatePickerConverter.convertLocalDateToPicker(it)
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    val localDate = selectedMillis?.let {
                        KonteninDatePickerConverter.convertPickerToLocalDate(it)
                    }
                    onDateSelected(localDate)
                }
            ) {
                Text(stringResource(R.string.action_select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Material 3 TimePickerDialog untuk memilih jam (FE-08).
 * Return value dalam menit sejak 00:00 (0-1439).
 *
 * Acuan: PRD §7 (jamMulaiMenit); DESIGN §5.2.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    initialHour: Int = 8,
    initialMinute: Int = 0,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val totalMinutes = timePickerState.hour * 60 + timePickerState.minute
                    onTimeSelected(totalMinutes)
                }
            ) {
                Text(stringResource(R.string.action_select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}
