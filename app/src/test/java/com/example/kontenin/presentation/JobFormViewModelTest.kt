package com.example.kontenin.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.presentation.jobform.FormValidationError
import com.example.kontenin.presentation.jobform.JobFormField
import com.example.kontenin.presentation.jobform.JobFormViewModel
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.util.FixedClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Unit test untuk interaksi JobFormViewModel (FE-09).
 */
class JobFormViewModelTest {

    private lateinit var gateway: DemoJobGateway
    private lateinit var viewModel: JobFormViewModel

    @Before
    fun setUp() {
        val clock = FixedClock(Instant.parse("2026-10-31T08:00:00Z"))
        gateway = DemoJobGateway(clock)
        viewModel = JobFormViewModel(
            jobGateway = gateway,
            savedStateHandle = SavedStateHandle(),
            jobId = 0L,
            mode = Routes.MODE_ADD
        )
    }

    @Test
    fun `simpan form kosong tidak menyimpan dan fokus menuju error pertama nama pengantin 1`() {
        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertFalse(state.errors.isEmpty())
        assertTrue(state.errors.containsKey(JobFormField.NAMA_PENGANTIN_1))
        assertEquals(JobFormField.NAMA_PENGANTIN_1, state.scrollToField)

        // Reset scroll trigger
        viewModel.onScrollToFieldConsumed()
        assertNull(viewModel.uiState.value.scrollToField)
    }

    @Test
    fun `lunas hanya aktif saat fee valid dan lebih dari 0`() {
        assertFalse(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("0")
        assertFalse(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("1000000")
        assertTrue(viewModel.uiState.value.isLunasEnabled)

        viewModel.onFeeChange("")
        assertFalse(viewModel.uiState.value.isLunasEnabled)
    }

    @Test
    fun `klik Lunas mengisi dibayar sama dengan fee dan menurunkan fee memunculkan error`() {
        // Set fee 1.000.000 dan dibayar 1.100.000
        viewModel.onFeeChange("1000000")
        viewModel.onDibayarChange("1100000")

        // Memunculkan error dibayar
        val stateWithExceed = viewModel.uiState.value
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            stateWithExceed.errors[JobFormField.DIBAYAR]
        )

        // Klik Lunas: dibayar = 1.000.000
        viewModel.onLunasClick()
        val stateAfterLunas = viewModel.uiState.value
        assertEquals("1000000", stateAfterLunas.dibayarText)
        assertNull(stateAfterLunas.errors[JobFormField.DIBAYAR])

        // Turunkan fee ke 800.000: error dibayar muncul kembali
        viewModel.onFeeChange("800000")
        val stateAfterFeeLowered = viewModel.uiState.value
        assertEquals(
            FormValidationError.DibayarExceedsFee,
            stateAfterFeeLowered.errors[JobFormField.DIBAYAR]
        )
    }

    @Test
    fun `validasi saat blur dan perbaikan input seketika menghilangkan error`() {
        // Nama diisi > 50 karakter lalu blur
        val longName = "A".repeat(55)
        viewModel.onNamaPengantin1Change(longName)
        viewModel.onFieldBlur(JobFormField.NAMA_PENGANTIN_1)

        assertEquals(
            FormValidationError.NamaPengantin1TooLong,
            viewModel.uiState.value.errors[JobFormField.NAMA_PENGANTIN_1]
        )

        // Perbaiki input menjadi valid
        viewModel.onNamaPengantin1Change("Andi")
        assertNull(viewModel.uiState.value.errors[JobFormField.NAMA_PENGANTIN_1])
    }
}
