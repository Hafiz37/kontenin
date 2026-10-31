package com.example.kontenin.ui.screens.jobform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.example.kontenin.R
import com.example.kontenin.presentation.jobform.JobFormUiState
import com.example.kontenin.ui.components.DateField
import com.example.kontenin.ui.components.DurationField
import com.example.kontenin.ui.components.KonteninTextField
import com.example.kontenin.ui.components.KonteninTonalButton
import com.example.kontenin.ui.components.MoneyField
import com.example.kontenin.ui.components.SectionHeading
import com.example.kontenin.ui.components.TimeField
import com.example.kontenin.ui.theme.KonteninTheme
import java.time.LocalDate

/**
 * Form sections untuk Job Form Screen (FE-08).
 *
 * Struktur seksi sesuai DESIGN §6.2:
 * 1. Pasangan
 * 2. Waktu
 * 3. Acara dan sumber
 * 4. Lokasi
 * 5. Pembayaran
 * 6. Catatan
 *
 * Acuan: PRD §7, FR-1.1–1.2; DESIGN §6.2.
 */

// ══════════════════════════════════════════════════════════════════════════
// 1. SEKSI PASANGAN
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun PasanganSection(
    uiState: JobFormUiState,
    onNamaPengantin1Change: (String) -> Unit,
    onNamaPengantin2Change: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_couple))
        Spacer(modifier = Modifier.height(spacing.space3))

        KonteninTextField(
            value = uiState.namaPengantin1,
            onValueChange = onNamaPengantin1Change,
            label = stringResource(R.string.field_name_1),
            placeholder = stringResource(R.string.field_name_1_hint),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        KonteninTextField(
            value = uiState.namaPengantin2,
            onValueChange = onNamaPengantin2Change,
            label = stringResource(R.string.field_name_2),
            placeholder = stringResource(R.string.field_name_2_hint),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 2. SEKSI WAKTU
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun WaktuSection(
    uiState: JobFormUiState,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit,
    onDurasiChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_time))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Tanggal
        DateFieldClickable(
            selectedDate = uiState.tanggalEpochDay?.let { LocalDate.ofEpochDay(it) },
            onShowPicker = onShowDatePicker,
            label = stringResource(R.string.field_date),
            helperText = stringResource(R.string.field_date_helper),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Jam mulai
        TimeFieldClickable(
            selectedTimeMinutes = uiState.jamMulaiMenit,
            onShowPicker = onShowTimePicker,
            label = stringResource(R.string.field_start_time),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Durasi
        DurationField(
            value = uiState.durasiText,
            onValueChange = onDurasiChange,
            label = stringResource(R.string.field_duration),
            helperText = stringResource(R.string.field_duration_helper),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Wrapper DateField dengan clickable behavior
@Composable
private fun DateFieldClickable(
    selectedDate: LocalDate?,
    onShowPicker: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    helperText: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier.clickable(
            onClick = onShowPicker,
            indication = null,
            interactionSource = interactionSource
        )
    ) {
        DateField(
            selectedDate = selectedDate,
            onDateSelected = { },
            label = label,
            helperText = helperText,
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Wrapper TimeField dengan clickable behavior
@Composable
private fun TimeFieldClickable(
    selectedTimeMinutes: Int?,
    onShowPicker: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Box(
        modifier = modifier.clickable(
            onClick = onShowPicker,
            indication = null,
            interactionSource = interactionSource
        )
    ) {
        TimeField(
            selectedTimeMinutes = selectedTimeMinutes,
            onTimeSelected = { },
            label = label,
            enabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 3. SEKSI ACARA DAN SUMBER
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun AcaraDanSumberSection(
    uiState: JobFormUiState,
    onAcaraChange: (String) -> Unit,
    onSumberJobChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_event_source))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Acara (autocomplete di FE-10)
        KonteninTextField(
            value = uiState.acara,
            onValueChange = onAcaraChange,
            label = stringResource(R.string.field_event),
            placeholder = stringResource(R.string.field_event_hint),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Sumber job (autocomplete di FE-10)
        KonteninTextField(
            value = uiState.sumberJob,
            onValueChange = onSumberJobChange,
            label = stringResource(R.string.field_source),
            placeholder = stringResource(R.string.field_source_hint),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 4. SEKSI LOKASI
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun LokasiSection(
    uiState: JobFormUiState,
    onAlamatChange: (String) -> Unit,
    onSharelocChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current
    val colors = KonteninTheme.colorScheme
    val typography = KonteninTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_location))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Alamat (opsional, max 300 karakter)
        KonteninTextField(
            value = uiState.alamat,
            onValueChange = onAlamatChange,
            label = stringResource(R.string.field_address),
            placeholder = stringResource(R.string.field_address_hint),
            helperText = if (uiState.showAlamatCounter) {
                stringResource(
                    R.string.char_counter_format,
                    uiState.alamatRemaining,
                    300
                )
            } else null,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Shareloc (opsional, URL)
        KonteninTextField(
            value = uiState.shareloc,
            onValueChange = onSharelocChange,
            label = stringResource(R.string.field_shareloc),
            placeholder = stringResource(R.string.field_shareloc_hint),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 5. SEKSI PEMBAYARAN
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun PembayaranSection(
    uiState: JobFormUiState,
    onFeeChange: (String) -> Unit,
    onDibayarChange: (String) -> Unit,
    onLunasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_payment))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Fee (wajib)
        MoneyField(
            valueText = uiState.feeText,
            onValueChange = onFeeChange,
            label = stringResource(R.string.field_fee),
            placeholder = "0",
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Sudah dibayar (opsional, default 0)
        MoneyField(
            valueText = uiState.dibayarText,
            onValueChange = onDibayarChange,
            label = stringResource(R.string.field_paid),
            placeholder = "0",
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.space3))

        // Tombol Lunas (tonal)
        KonteninTonalButton(
            text = stringResource(R.string.action_paid),
            onClick = onLunasClick,
            enabled = uiState.feeText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 6. SEKSI CATATAN
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun CatatanSection(
    uiState: JobFormUiState,
    onCatatanChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = KonteninTheme.spacing
    val colors = KonteninTheme.colorScheme
    val typography = KonteninTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_notes))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Catatan (opsional, max 1000 karakter)
        KonteninTextField(
            value = uiState.catatan,
            onValueChange = onCatatanChange,
            label = stringResource(R.string.field_notes),
            placeholder = stringResource(R.string.field_notes_hint),
            helperText = if (uiState.showCatatanCounter) {
                stringResource(
                    R.string.char_counter_format,
                    uiState.catatanRemaining,
                    1000
                )
            } else null,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
