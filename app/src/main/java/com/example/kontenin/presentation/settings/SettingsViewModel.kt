package com.example.kontenin.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kontenin.contract.BackupGateway
import com.example.kontenin.contract.ExternalActionGateway
import com.example.kontenin.contract.OpenSystemSettingsResult
import com.example.kontenin.contract.PermissionStatus
import com.example.kontenin.contract.ReminderGateway
import com.example.kontenin.contract.TestReminderResult
import com.example.kontenin.demo.DemoBackupGateway
import com.example.kontenin.demo.DemoJobGateway
import com.example.kontenin.demo.DemoReminderGateway
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Layar Pengaturan (FE-16, FE-17).
 *
 * Mengelola:
 * - Status izin notifikasi & alarm, pembukaan pengaturan sistem,
 *   simulasi Uji Pengingat, dialog baterai, dan info versi aplikasi (FE-16).
 * - State machine dan alur ekspor / impor data simulasi melalui BackupController (FE-17).
 */
class SettingsViewModel(
    private val reminderGateway: ReminderGateway,
    private val externalActionGateway: ExternalActionGateway,
    val backupGateway: BackupGateway = DemoBackupGateway(DemoJobGateway(SystemClock())),
    val clock: AppClock = SystemClock(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val backupController: BackupController = BackupController(
        backupGateway = backupGateway,
        clock = clock,
        coroutineScope = viewModelScope,
        dispatcher = dispatcher
    )
    val backupUiState: StateFlow<BackupUiState> = backupController.uiState

    init {
        loadPermissions()
    }

    fun loadPermissions() {
        viewModelScope.launch(dispatcher) {
            val notifStatus = reminderGateway.getNotificationPermissionStatus()
            val alarmStatus = reminderGateway.getExactAlarmPermissionStatus()
            _uiState.update {
                it.copy(
                    notificationPermissionStatus = notifStatus,
                    exactAlarmPermissionStatus = alarmStatus
                )
            }
        }
    }

    fun onOpenSettingsClick() {
        viewModelScope.launch(dispatcher) {
            val result = externalActionGateway.openSystemSettings()
            when (result) {
                is OpenSystemSettingsResult.Success -> {
                    // Refresh status setelah kembali dari settings
                    loadPermissions()
                }
                is OpenSystemSettingsResult.Error -> {
                    _uiState.update { it.copy(userMessage = result.message) }
                }
            }
        }
    }

    fun onTestReminderClick() {
        viewModelScope.launch(dispatcher) {
            val result = reminderGateway.testReminder()
            val message = when (result) {
                is TestReminderResult.Scheduled, is TestReminderResult.Success -> {
                    "Notifikasi uji dijadwalkan (simulasi demo, tanpa alarm OS)."
                }
                is TestReminderResult.PermissionDenied -> {
                    "Izin notifikasi belum diberikan. Pengingat tidak dapat dijadwalkan."
                }
                is TestReminderResult.Failed -> {
                    "Gagal menjadwalkan notifikasi uji: ${result.message}"
                }
                is TestReminderResult.Error -> {
                    "Gagal menjadwalkan notifikasi uji: ${result.message}"
                }
            }
            _uiState.update {
                it.copy(
                    testReminderMessage = message,
                    userMessage = message
                )
            }
        }
    }

    fun onShowBatteryHelpDialog(show: Boolean) {
        _uiState.update { it.copy(showBatteryHelpDialog = show) }
    }

    fun onToggleNotificationPermission() {
        viewModelScope.launch(dispatcher) {
            val current = _uiState.value.notificationPermissionStatus
            val newStatus = if (current == PermissionStatus.Granted) {
                PermissionStatus.Denied
            } else {
                PermissionStatus.Granted
            }
            if (reminderGateway is DemoReminderGateway) {
                reminderGateway.setNotificationPermission(newStatus)
            }
            _uiState.update { it.copy(notificationPermissionStatus = newStatus) }
        }
    }

    fun onToggleExactAlarmPermission() {
        viewModelScope.launch(dispatcher) {
            val current = _uiState.value.exactAlarmPermissionStatus
            val newStatus = if (current == PermissionStatus.Granted) {
                PermissionStatus.Denied
            } else {
                PermissionStatus.Granted
            }
            if (reminderGateway is DemoReminderGateway) {
                reminderGateway.setExactAlarmPermission(newStatus)
            }
            _uiState.update { it.copy(exactAlarmPermissionStatus = newStatus) }
        }
    }

    fun setNotificationPermission(status: PermissionStatus) {
        if (reminderGateway is DemoReminderGateway) {
            reminderGateway.setNotificationPermission(status)
        }
        _uiState.update { it.copy(notificationPermissionStatus = status) }
    }

    fun setExactAlarmPermission(status: PermissionStatus) {
        if (reminderGateway is DemoReminderGateway) {
            reminderGateway.setExactAlarmPermission(status)
        }
        _uiState.update { it.copy(exactAlarmPermissionStatus = status) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun clearTestReminderMessage() {
        _uiState.update { it.copy(testReminderMessage = null) }
    }

    // ── Delegasi Aksi Cadangan (FE-17) ──────────────────────────────────────

    fun onExportClick() = backupController.onExportClick()
    fun dismissExportDialog() = backupController.dismissExportDialog()
    fun onCancelExport() = backupController.onCancelExport()
    fun executeExport(simulatedDestination: String? = null, simulateFail: Boolean = false) =
        backupController.executeExport(simulatedDestination, simulateFail)

    fun onImportClick() = backupController.onImportClick()
    fun dismissImportChooserDialog() = backupController.dismissImportChooserDialog()
    fun onSelectImportFixture(fixtureId: String) = backupController.onSelectImportFixture(fixtureId)
    fun dismissImportConfirmDialog() = backupController.dismissImportConfirmDialog()
    fun confirmImport() = backupController.confirmImport()
    fun clearBackupSnackbar() = backupController.clearSnackbarEvent()
}
