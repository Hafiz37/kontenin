package com.example.kontenin.presentation

import com.example.kontenin.contract.*
import com.example.kontenin.demo.DemoExternalActionGateway
import com.example.kontenin.demo.DemoReminderGateway
import com.example.kontenin.demo.DemoScenario
import com.example.kontenin.presentation.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit Test untuk SettingsViewModel (FE-16).
 *
 * Menguji:
 * - Status izin notifikasi & alarm tepat waktu (teks + ikon, banner di Pengaturan)
 * - Buka Pengaturan via ExternalActionGateway
 * - Uji Pengingat (dijadwalkan, izin ditolak, gagal)
 * - Dialog petunjuk baterai
 */
class SettingsViewModelTest {

    private lateinit var reminderGateway: DemoReminderGateway
    private lateinit var externalActionGateway: DemoExternalActionGateway
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        reminderGateway = DemoReminderGateway()
        externalActionGateway = DemoExternalActionGateway()
        viewModel = SettingsViewModel(
            reminderGateway = reminderGateway,
            externalActionGateway = externalActionGateway,
            dispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `status izin default belum aktif memunculkan banner peringatan di Pengaturan`() {
        val state = viewModel.uiState.value
        assertEquals(PermissionStatus.NotRequested, state.notificationPermissionStatus)
        assertEquals(PermissionStatus.NotRequested, state.exactAlarmPermissionStatus)
        assertFalse(state.isNotificationGranted)
        assertFalse(state.isExactAlarmGranted)
        assertTrue(state.hasPermissionIssue)
    }

    @Test
    fun `kedua izin aktif menghilangkan banner peringatan`() {
        viewModel.setNotificationPermission(PermissionStatus.Granted)
        viewModel.setExactAlarmPermission(PermissionStatus.Granted)

        val state = viewModel.uiState.value
        assertTrue(state.isNotificationGranted)
        assertTrue(state.isExactAlarmGranted)
        assertFalse(state.hasPermissionIssue)
    }

    @Test
    fun `toggle izin mengubah status dan banner secara reaktif`() {
        // Awalnya NotRequested -> toggle menjadi Granted
        viewModel.onToggleNotificationPermission()
        assertTrue(viewModel.uiState.value.isNotificationGranted)

        // Toggle kembali menjadi Denied
        viewModel.onToggleNotificationPermission()
        assertFalse(viewModel.uiState.value.isNotificationGranted)
        assertEquals(PermissionStatus.Denied, viewModel.uiState.value.notificationPermissionStatus)
        assertTrue(viewModel.uiState.value.hasPermissionIssue)
    }

    @Test
    fun `buka pengaturan memanggil externalActionGateway`() = runBlocking {
        viewModel.onOpenSettingsClick()
        // Tidak ada error message jika berhasil
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun `buka pengaturan gagal menghasilkan error userMessage`() = runBlocking {
        externalActionGateway.scenario = DemoScenario.SAVE_ERROR
        viewModel.onOpenSettingsClick()
        assertNotNull(viewModel.uiState.value.userMessage)
        assertEquals("Simulasi gagal buka Settings", viewModel.uiState.value.userMessage)
    }

    @Test
    fun `uji pengingat saat izin belum aktif menghasilkan status ditolak`() = runBlocking {
        viewModel.setNotificationPermission(PermissionStatus.Denied)
        viewModel.onTestReminderClick()

        val state = viewModel.uiState.value
        assertNotNull(state.testReminderMessage)
        assertTrue(state.testReminderMessage!!.contains("Izin notifikasi belum diberikan"))
    }

    @Test
    fun `uji pengingat saat izin aktif menghasilkan status dijadwalkan`() = runBlocking {
        viewModel.setNotificationPermission(PermissionStatus.Granted)
        viewModel.onTestReminderClick()

        val state = viewModel.uiState.value
        assertNotNull(state.testReminderMessage)
        assertTrue(state.testReminderMessage!!.contains("Notifikasi uji dijadwalkan"))
        assertTrue(state.testReminderMessage!!.contains("simulasi demo, tanpa alarm OS"))
    }

    @Test
    fun `uji pengingat saat terjadi error sistem simulasi menghasilkan status gagal`() = runBlocking {
        viewModel.setNotificationPermission(PermissionStatus.Granted)
        reminderGateway.simulateTestReminderFailure = true
        viewModel.onTestReminderClick()

        val state = viewModel.uiState.value
        assertNotNull(state.testReminderMessage)
        assertTrue(state.testReminderMessage!!.contains("Gagal menjadwalkan notifikasi uji"))
    }

    @Test
    fun `tampilkan dan sembunyikan dialog baterai`() {
        assertFalse(viewModel.uiState.value.showBatteryHelpDialog)
        viewModel.onShowBatteryHelpDialog(true)
        assertTrue(viewModel.uiState.value.showBatteryHelpDialog)
        viewModel.onShowBatteryHelpDialog(false)
        assertFalse(viewModel.uiState.value.showBatteryHelpDialog)
    }
}
