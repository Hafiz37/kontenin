package com.example.kontenin.presentation.settings

import com.example.kontenin.R
import com.example.kontenin.contract.PermissionStatus

/**
 * State UI untuk Layar Pengaturan (FE-16).
 *
 * Mengelola status izin notifikasi & alarm tepat waktu,
 * banner peringatan (hanya muncul di Pengaturan jika ada masalah izin),
 * dialog petunjuk baterai, hasil uji pengingat, dan info versi aplikasi.
 */
data class SettingsUiState(
    val notificationPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val exactAlarmPermissionStatus: PermissionStatus = PermissionStatus.NotRequested,
    val showBatteryHelpDialog: Boolean = false,
    val testReminderMessage: String? = null,
    val userMessage: String? = null,
    val appVersion: String = "0.1.0-alpha"
) {
    val isNotificationGranted: Boolean
        get() = notificationPermissionStatus == PermissionStatus.Granted

    val isExactAlarmGranted: Boolean
        get() = exactAlarmPermissionStatus == PermissionStatus.Granted

    val hasPermissionIssue: Boolean
        get() = !isNotificationGranted || !isExactAlarmGranted

    val bannerTitleRes: Int
        get() = if (!isNotificationGranted) {
            R.string.permission_banner_title
        } else {
            R.string.permission_exact_alarm_title
        }

    val bannerDescRes: Int
        get() = if (!isNotificationGranted) {
            R.string.permission_banner_desc
        } else {
            R.string.permission_exact_alarm_desc
        }
}
