package com.example.kontenin.ui.screens.jobform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import com.example.kontenin.R
import com.example.kontenin.presentation.jobform.FormMode
import com.example.kontenin.presentation.jobform.JobFormField
import com.example.kontenin.presentation.jobform.JobFormViewModel
import com.example.kontenin.ui.components.DatePickerModal
import com.example.kontenin.ui.components.KonteninPrimaryButton
import com.example.kontenin.ui.components.TimePickerModal
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.ui.theme.KonteninTheme
import java.time.LocalDate

/**
 * Layar Form Job dengan semua field input dan validasi (FE-08, FE-09).
 *
 * Mendukung tiga mode melalui argument navigasi:
 * - [Routes.MODE_ADD]       → judul "Tambah Job", jobId diabaikan
 * - [Routes.MODE_EDIT]      → judul "Ubah Job", jobId = target
 * - [Routes.MODE_DUPLICATE] → judul dinamis "Tambah Job (Disalin dari X & Y)"
 *
 * Fitur FE-09:
 * - Validasi ber-tipe untuk semua field PRD FR-1.2
 * - Error inline di bawah masing-masing kolom dengan string dari strings.xml
 * - Validasi saat blur (kehilangan fokus) untuk kolom yang sudah diisi
 * - Pembaruan error seketika saat input diperbaiki
 * - Validasi seluruh form saat Simpan & gulir/fokus otomatis ke error pertama
 * - Tombol Lunas mengisi dibayar = fee, aktif hanya bila fee valid dan > 0
 *
 * Acuan: PRD §7, FR-1.1–1.2, FR-3.2, OQ-8; DESIGN §6.2, §7.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFormScreen(
    viewModel: JobFormViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors  = KonteninTheme.colorScheme
    val spacing = KonteninTheme.spacing
    val dim     = KonteninTheme.dimensions

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Map focus requesters untuk tiap field form
    val focusRequesters = remember {
        JobFormField.entries.associateWith { FocusRequester() }
    }

    // Gulir dan fokus ke error pertama saat validasi Simpan gagal (FE-09)
    LaunchedEffect(uiState.scrollToField) {
        val targetField = uiState.scrollToField ?: return@LaunchedEffect
        focusRequesters[targetField]?.requestFocus()
        viewModel.onScrollToFieldConsumed()
    }

    // Navigasi kembali setelah berhasil simpan (FE-11, FE-12)
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    // Judul dinamis berdasarkan mode
    val screenTitle = when (uiState.mode) {
        FormMode.Edit -> stringResource(R.string.screen_edit_job)
        FormMode.Duplicate -> {
            uiState.sourceJobName?.let {
                stringResource(R.string.screen_add_job_from, it)
            } ?: stringResource(R.string.screen_add_job)
        }
        else -> stringResource(R.string.screen_add_job)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier,
                    ) {
                        Icon(
                            imageVector        = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_close),
                        )
                    }
                },
                title = {
                    Text(
                        text  = screenTitle,
                        style = KonteninTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor         = colors.surface,
                    navigationIconContentColor = colors.onSurface,
                    titleContentColor      = colors.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Area scroll konten form ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .imePadding()                        // Keyboard inset
                    .padding(horizontal = spacing.space4),
            ) {
                Spacer(modifier = Modifier.height(spacing.space4))

                // 1. Seksi Pasangan
                PasanganSection(
                    uiState = uiState,
                    onNamaPengantin1Change = viewModel::onNamaPengantin1Change,
                    onNamaPengantin2Change = viewModel::onNamaPengantin2Change,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters
                )

                Spacer(modifier = Modifier.height(spacing.space5))

                // 2. Seksi Waktu
                WaktuSection(
                    uiState = uiState,
                    onShowDatePicker = viewModel::showDatePicker,
                    onShowTimePicker = viewModel::showTimePicker,
                    onDurasiChange = viewModel::onDurasiChange,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters
                )

                Spacer(modifier = Modifier.height(spacing.space5))

                // 3. Seksi Acara dan Sumber (Autocomplete FE-10)
                AcaraDanSumberSection(
                    uiState = uiState,
                    onAcaraChange = viewModel::onAcaraChange,
                    onSumberJobChange = viewModel::onSumberJobChange,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters,
                    onAcaraFocus = viewModel::onAcaraFocus,
                    onAcaraSuggestionSelected = viewModel::onAcaraSuggestionSelected,
                    onAcaraDismissSuggestions = viewModel::onAcaraDismissSuggestions,
                    onSumberJobFocus = viewModel::onSumberJobFocus,
                    onSumberJobSuggestionSelected = viewModel::onSumberJobSuggestionSelected,
                    onSumberJobDismissSuggestions = viewModel::onSumberJobDismissSuggestions
                )

                Spacer(modifier = Modifier.height(spacing.space5))

                // 4. Seksi Lokasi
                LokasiSection(
                    uiState = uiState,
                    onAlamatChange = viewModel::onAlamatChange,
                    onSharelocChange = viewModel::onSharelocChange,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters
                )

                Spacer(modifier = Modifier.height(spacing.space5))

                // 5. Seksi Pembayaran
                PembayaranSection(
                    uiState = uiState,
                    onFeeChange = viewModel::onFeeChange,
                    onDibayarChange = viewModel::onDibayarChange,
                    onLunasClick = viewModel::onLunasClick,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters
                )

                Spacer(modifier = Modifier.height(spacing.space5))

                // 6. Seksi Catatan
                CatatanSection(
                    uiState = uiState,
                    onCatatanChange = viewModel::onCatatanChange,
                    onFieldBlur = viewModel::onFieldBlur,
                    focusRequesters = focusRequesters
                )

                Spacer(modifier = Modifier.height(spacing.space6))
            }

            // ── Sticky Simpan — selalu terlihat di bawah, di luar scroll ─────
            Surface(
                shadowElevation = dim.fabElevation,
                color           = colors.surface,
            ) {
                KonteninPrimaryButton(
                    text     = stringResource(R.string.action_save),
                    onClick  = viewModel::onSaveClick,
                    enabled  = !uiState.isLoading,
                    loading  = uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = spacing.space4, vertical = spacing.space3),
                )
            }
        }
    }

    // ── Date Picker Modal ───────────────────────────────────────────────────
    if (uiState.showDatePicker) {
        DatePickerModal(
            onDateSelected = { date ->
                viewModel.onTanggalSelected(date?.toEpochDay())
            },
            onDismiss = viewModel::hideDatePicker,
            initialDate = uiState.tanggalEpochDay?.let { LocalDate.ofEpochDay(it) }
        )
    }

    // ── Time Picker Modal ───────────────────────────────────────────────────
    if (uiState.showTimePicker) {
        val currentMinutes = uiState.jamMulaiMenit ?: 480 // Default 08:00
        val hour = currentMinutes / 60
        val minute = currentMinutes % 60

        TimePickerModal(
            onTimeSelected = { totalMinutes ->
                viewModel.onJamMulaiSelected(totalMinutes)
            },
            onDismiss = viewModel::hideTimePicker,
            initialHour = hour,
            initialMinute = minute
        )
    }

    // ── Dialog Peringatan Jadwal Bertabrakan (FE-12) ────────────────────────
    if (uiState.showConflictDialog && uiState.conflictingJobs.isNotEmpty()) {
        ConflictDialog(
            conflicts = uiState.conflictingJobs,
            onDismiss = viewModel::onConflictDismiss,
            onConfirm = viewModel::onConflictForceSave
        )
    }
}
