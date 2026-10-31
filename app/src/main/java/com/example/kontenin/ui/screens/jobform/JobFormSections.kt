package com.example.kontenin.ui.screens.jobform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.example.kontenin.R
import com.example.kontenin.presentation.jobform.JobFormField
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
 * Modifier helper untuk memicu aksi blur (kehilangan fokus) setelah kolom pernah mendapat fokus.
 */
@Composable
fun Modifier.onFocusLost(onBlur: () -> Unit): Modifier {
    var hadFocus by remember { mutableStateOf(false) }
    return this.onFocusChanged { focusState ->
        if (focusState.isFocused) {
            hadFocus = true
        } else if (hadFocus) {
            hadFocus = false
            onBlur()
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// 1. SEKSI PASANGAN
// ══════════════════════════════════════════════════════════════════════════

@Composable
fun PasanganSection(
    uiState: JobFormUiState,
    onNamaPengantin1Change: (String) -> Unit,
    onNamaPengantin2Change: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    val err1 = uiState.errors[JobFormField.NAMA_PENGANTIN_1]?.let { stringResource(it.messageResId) }
    val req1 = focusRequesters[JobFormField.NAMA_PENGANTIN_1]

    val err2 = uiState.errors[JobFormField.NAMA_PENGANTIN_2]?.let { stringResource(it.messageResId) }
    val req2 = focusRequesters[JobFormField.NAMA_PENGANTIN_2]

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_couple))
        Spacer(modifier = Modifier.height(spacing.space3))

        KonteninTextField(
            value = uiState.namaPengantin1,
            onValueChange = onNamaPengantin1Change,
            label = stringResource(R.string.field_name_1),
            placeholder = stringResource(R.string.field_name_1_hint),
            singleLine = true,
            errorMessage = err1,
            isError = err1 != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (req1 != null) Modifier.focusRequester(req1) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.NAMA_PENGANTIN_1) }
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        KonteninTextField(
            value = uiState.namaPengantin2,
            onValueChange = onNamaPengantin2Change,
            label = stringResource(R.string.field_name_2),
            placeholder = stringResource(R.string.field_name_2_hint),
            singleLine = true,
            errorMessage = err2,
            isError = err2 != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (req2 != null) Modifier.focusRequester(req2) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.NAMA_PENGANTIN_2) }
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
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    val errTanggal = uiState.errors[JobFormField.TANGGAL]?.let { stringResource(it.messageResId) }
    val reqTanggal = focusRequesters[JobFormField.TANGGAL]

    val errJam = uiState.errors[JobFormField.JAM_MULAI]?.let { stringResource(it.messageResId) }
    val reqJam = focusRequesters[JobFormField.JAM_MULAI]

    val errDurasi = uiState.errors[JobFormField.DURASI]?.let { stringResource(it.messageResId) }
    val reqDurasi = focusRequesters[JobFormField.DURASI]

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_time))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Tanggal
        DateFieldClickable(
            selectedDate = uiState.tanggalEpochDay?.let { LocalDate.ofEpochDay(it) },
            onShowPicker = onShowDatePicker,
            label = stringResource(R.string.field_date),
            helperText = stringResource(R.string.field_date_helper),
            errorMessage = errTanggal,
            isError = errTanggal != null,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqTanggal != null) Modifier.focusRequester(reqTanggal) else Modifier)
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Jam mulai
        TimeFieldClickable(
            selectedTimeMinutes = uiState.jamMulaiMenit,
            onShowPicker = onShowTimePicker,
            label = stringResource(R.string.field_start_time),
            errorMessage = errJam,
            isError = errJam != null,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqJam != null) Modifier.focusRequester(reqJam) else Modifier)
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Durasi
        DurationField(
            value = uiState.durasiText,
            onValueChange = onDurasiChange,
            label = stringResource(R.string.field_duration),
            helperText = stringResource(R.string.field_duration_helper),
            errorMessage = errDurasi,
            isError = errDurasi != null,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqDurasi != null) Modifier.focusRequester(reqDurasi) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.DURASI) }
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
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
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
            errorMessage = errorMessage,
            isError = isError,
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
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
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
            errorMessage = errorMessage,
            isError = isError,
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
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    val errAcara = uiState.errors[JobFormField.ACARA]?.let { stringResource(it.messageResId) }
    val reqAcara = focusRequesters[JobFormField.ACARA]

    val errSumber = uiState.errors[JobFormField.SUMBER_JOB]?.let { stringResource(it.messageResId) }
    val reqSumber = focusRequesters[JobFormField.SUMBER_JOB]

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_event_source))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Acara
        KonteninTextField(
            value = uiState.acara,
            onValueChange = onAcaraChange,
            label = stringResource(R.string.field_event),
            placeholder = stringResource(R.string.field_event_hint),
            singleLine = true,
            errorMessage = errAcara,
            isError = errAcara != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqAcara != null) Modifier.focusRequester(reqAcara) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.ACARA) }
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Sumber job
        KonteninTextField(
            value = uiState.sumberJob,
            onValueChange = onSumberJobChange,
            label = stringResource(R.string.field_source),
            placeholder = stringResource(R.string.field_source_hint),
            singleLine = true,
            errorMessage = errSumber,
            isError = errSumber != null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqSumber != null) Modifier.focusRequester(reqSumber) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.SUMBER_JOB) }
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
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    val errAlamat = uiState.errors[JobFormField.ALAMAT]?.let { stringResource(it.messageResId) }
    val reqAlamat = focusRequesters[JobFormField.ALAMAT]

    val errShareloc = uiState.errors[JobFormField.SHARELOC]?.let { stringResource(it.messageResId) }
    val reqShareloc = focusRequesters[JobFormField.SHARELOC]

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
            errorMessage = errAlamat,
            isError = errAlamat != null,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqAlamat != null) Modifier.focusRequester(reqAlamat) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.ALAMAT) }
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Shareloc (opsional, URL)
        KonteninTextField(
            value = uiState.shareloc,
            onValueChange = onSharelocChange,
            label = stringResource(R.string.field_shareloc),
            placeholder = stringResource(R.string.field_shareloc_hint),
            singleLine = true,
            errorMessage = errShareloc,
            isError = errShareloc != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqShareloc != null) Modifier.focusRequester(reqShareloc) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.SHARELOC) }
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
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing
    val focusManager = LocalFocusManager.current

    val errFee = uiState.errors[JobFormField.FEE]?.let { stringResource(it.messageResId) }
    val reqFee = focusRequesters[JobFormField.FEE]

    val errDibayar = uiState.errors[JobFormField.DIBAYAR]?.let { stringResource(it.messageResId) }
    val reqDibayar = focusRequesters[JobFormField.DIBAYAR]

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeading(title = stringResource(R.string.section_payment))
        Spacer(modifier = Modifier.height(spacing.space3))

        // Fee (wajib)
        MoneyField(
            valueText = uiState.feeText,
            onValueChange = onFeeChange,
            label = stringResource(R.string.field_fee),
            placeholder = "0",
            errorMessage = errFee,
            isError = errFee != null,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqFee != null) Modifier.focusRequester(reqFee) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.FEE) }
        )

        Spacer(modifier = Modifier.height(spacing.space4))

        // Sudah dibayar (opsional, default 0)
        MoneyField(
            valueText = uiState.dibayarText,
            onValueChange = onDibayarChange,
            label = stringResource(R.string.field_paid),
            placeholder = "0",
            errorMessage = errDibayar,
            isError = errDibayar != null,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqDibayar != null) Modifier.focusRequester(reqDibayar) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.DIBAYAR) }
        )

        Spacer(modifier = Modifier.height(spacing.space3))

        // Tombol Lunas (tonal) - Aktif hanya saat fee valid dan > 0 (OQ-8)
        KonteninTonalButton(
            text = stringResource(R.string.action_paid),
            onClick = onLunasClick,
            enabled = uiState.isLunasEnabled,
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
    modifier: Modifier = Modifier,
    onFieldBlur: (JobFormField) -> Unit = {},
    focusRequesters: Map<JobFormField, FocusRequester> = emptyMap()
) {
    val spacing = KonteninTheme.spacing

    val errCatatan = uiState.errors[JobFormField.CATATAN]?.let { stringResource(it.messageResId) }
    val reqCatatan = focusRequesters[JobFormField.CATATAN]

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
            errorMessage = errCatatan,
            isError = errCatatan != null,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (reqCatatan != null) Modifier.focusRequester(reqCatatan) else Modifier)
                .onFocusLost { onFieldBlur(JobFormField.CATATAN) }
        )
    }
}
