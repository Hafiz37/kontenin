package com.example.kontenin.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.kontenin.contract.PermissionStatus
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.demo.DemoReminderGateway
import com.example.kontenin.presentation.jobform.JobFormViewModel
import com.example.kontenin.ui.navigation.Routes
import com.example.kontenin.util.FixedClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

/**
 * Unit Test untuk pengingat dan izin saat simpan job (FE-16).
 *
 * Menguji:
 * - Penjelasan izin notifikasi saat simpan pertama (Lanjut, Nanti, Tolak)
 * - Job tetap tersimpan saat izin ditolak
 * - Deteksi pengingat yang dilewati (skipped reminders)
 * - Simulasi ketuk notifikasi untuk ID valid vs ID dihapus (EC-12)
 */
class JobFormReminderTest {

    private val fixedInstant = Instant.parse("2026-10-31T08:00:00Z")
    private val clock = FixedClock(fixedInstant)
    private lateinit var jobGateway: DemoJobGateway
    private lateinit var reminderGateway: DemoReminderGateway

    @Before
    fun setUp() {
        jobGateway = DemoJobGateway(clock)
        reminderGateway = DemoReminderGateway(clock)
    }

    private fun createViewModel(mode: String = Routes.MODE_ADD, jobId: Long = 0L): JobFormViewModel {
        return JobFormViewModel(
            jobGateway = jobGateway,
            savedStateHandle = SavedStateHandle(),
            jobId = jobId,
            mode = mode,
            reminderGateway = reminderGateway,
            dispatcher = Dispatchers.Unconfined
        )
    }

    private fun fillValidForm(
        viewModel: JobFormViewModel,
        tanggalEpochDay: Long = LocalDate.of(2026, 11, 20).toEpochDay(),
        jamMulaiMenit: Int = 600, // 10:00
        durasiText: String = "4"
    ) {
        viewModel.onNamaPengantin1Change("Budi")
        viewModel.onNamaPengantin2Change("Rina")
        viewModel.onTanggalSelected(tanggalEpochDay)
        viewModel.onJamMulaiSelected(jamMulaiMenit)
        viewModel.onDurasiChange(durasiText)
        viewModel.onAcaraChange("Resepsi")
        viewModel.onSumberJobChange("Vendor A")
        viewModel.onFeeChange("2000000")
        viewModel.onDibayarChange("0")
    }

    @Test
    fun `simpan pertama memunculkan dialog penjelasan notifikasi saat izin NotRequested`() = runBlocking {
        assertEquals(PermissionStatus.NotRequested, reminderGateway.getNotificationPermissionStatus())
        val viewModel = createViewModel()
        fillValidForm(viewModel)

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertTrue("Dialog penjelasan notifikasi harus muncul", state.showNotificationRationaleDialog)
        assertFalse("Job belum tersimpan sebelum pengguna memilih respon dialog", state.isSaved)
    }

    @Test
    fun `simpan pertama lalu pilih Lanjut memberikan izin dan menyimpan job`() = runBlocking {
        val viewModel = createViewModel()
        fillValidForm(viewModel)

        viewModel.onSaveClick()
        assertTrue(viewModel.uiState.value.showNotificationRationaleDialog)

        // Pengguna memilih Lanjut
        viewModel.onRationaleContinue()

        val state = viewModel.uiState.value
        assertFalse(state.showNotificationRationaleDialog)
        assertTrue(state.isSaved)
        assertEquals(PermissionStatus.Granted, reminderGateway.getNotificationPermissionStatus())

        // Pastikan tersimpan di gateway
        val allJobs = jobGateway.observeAllJobs().first()
        assertTrue(allJobs.any { it.namaPengantin1 == "Budi" && it.namaPengantin2 == "Rina" })
    }

    @Test
    fun `simpan pertama lalu pilih Nanti tetap menyimpan job`() = runBlocking {
        val viewModel = createViewModel()
        fillValidForm(viewModel)

        viewModel.onSaveClick()
        assertTrue(viewModel.uiState.value.showNotificationRationaleDialog)

        // Pengguna memilih Nanti
        viewModel.onRationaleLater()

        val state = viewModel.uiState.value
        assertFalse(state.showNotificationRationaleDialog)
        assertTrue(state.isSaved)

        // Pastikan job tetap tersimpan
        val allJobs = jobGateway.observeAllJobs().first()
        assertTrue(allJobs.any { it.namaPengantin1 == "Budi" && it.namaPengantin2 == "Rina" })
    }

    @Test
    fun `simpan pertama lalu tolak izin, job tetap ada dan izin tercatat ditolak`() = runBlocking {
        val viewModel = createViewModel()
        fillValidForm(viewModel)

        viewModel.onSaveClick()
        assertTrue(viewModel.uiState.value.showNotificationRationaleDialog)

        // Pengguna menolak izin notifikasi
        viewModel.onRationaleDeny()

        val state = viewModel.uiState.value
        assertFalse(state.showNotificationRationaleDialog)
        assertTrue(state.isSaved)
        assertEquals(PermissionStatus.Denied, reminderGateway.getNotificationPermissionStatus())

        // Pastikan job tetap ada di gateway (EC-9)
        val allJobs = jobGateway.observeAllJobs().first()
        assertTrue(allJobs.any { it.namaPengantin1 == "Budi" && it.namaPengantin2 == "Rina" })
    }

    @Test
    fun `simpan job dengan acara yang terlalu dekat menandai pengingat dilewati`() = runBlocking {
        reminderGateway.setNotificationPermission(PermissionStatus.Granted)
        val viewModel = createViewModel()

        // Acara hari ini 2026-10-31 pukul 10:00 (saat clock adalah 2026-10-31 08:00 UTC)
        // Maka H-7, H-3, H-1 pukul 08:00 dan 3 jam sebelum acara sudah lewat!
        val todayEpochDay = LocalDate.of(2026, 10, 31).toEpochDay()
        fillValidForm(viewModel, tanggalEpochDay = todayEpochDay, jamMulaiMenit = 600)

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertTrue("Pengingat harus ditandai dilewati", state.hasSkippedReminders)
        assertTrue(state.skippedRemindersCount > 0)
    }

    @Test
    fun `simpan job dengan acara 1 bulan ke depan menjadwalkan semua pengingat tanpa dilewati`() = runBlocking {
        reminderGateway.setNotificationPermission(PermissionStatus.Granted)
        val viewModel = createViewModel()

        // Acara 30 hari ke depan
        val futureEpochDay = LocalDate.of(2026, 11, 30).toEpochDay()
        fillValidForm(viewModel, tanggalEpochDay = futureEpochDay, jamMulaiMenit = 600)

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse("Tidak ada pengingat yang dilewati", state.hasSkippedReminders)
        assertEquals(0, state.skippedRemindersCount)
    }

    @Test
    fun `simulasi ketuk notifikasi membuka job jika ada dan mengembalikan null jika job sudah dihapus`() = runBlocking {
        // Ambil job pertama dari seed
        val firstJob = jobGateway.observeAllJobs().first().first()
        val validJobId = firstJob.id

        // ID valid: ditemukan
        val foundJob = jobGateway.observeJob(validJobId).first()
        assertNotNull(foundJob)
        assertEquals(validJobId, foundJob!!.id)

        // Hapus job tersebut
        jobGateway.deleteJob(validJobId)

        // ID dihapus (EC-12): null -> UI menampilkan pesan error dan kembali ke Beranda
        val deletedJob = jobGateway.observeJob(validJobId).first()
        assertNull(deletedJob)
    }
}
