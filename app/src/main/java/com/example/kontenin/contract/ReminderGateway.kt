package com.example.kontenin.contract

sealed interface PermissionStatus {
    data object Granted : PermissionStatus
    data object Denied : PermissionStatus
    data object NotRequested : PermissionStatus
}

sealed interface RequestPermissionResult {
    data object Granted : RequestPermissionResult
    data object Denied : RequestPermissionResult
    data object Cancelled : RequestPermissionResult
}

sealed interface TestReminderResult {
    data object Scheduled : TestReminderResult
    data object PermissionDenied : TestReminderResult
    data class Failed(val message: String) : TestReminderResult

    // Legacy aliases for backward compatibility
    data object Success : TestReminderResult
    data class Error(val message: String) : TestReminderResult
}

data class ReminderScheduleResult(
    val scheduledCount: Int,
    val skippedCount: Int
)

interface ReminderGateway {
    suspend fun getNotificationPermissionStatus(): PermissionStatus
    suspend fun getExactAlarmPermissionStatus(): PermissionStatus

    // Default/Legacy helpers
    suspend fun getPermissionStatus(): PermissionStatus = getNotificationPermissionStatus()

    suspend fun requestNotificationPermission(): RequestPermissionResult
    suspend fun requestExactAlarmPermission(): RequestPermissionResult
    suspend fun requestPermission(): RequestPermissionResult = requestNotificationPermission()

    suspend fun testReminder(): TestReminderResult

    suspend fun scheduleJobReminders(
        tanggalEpochDay: Long,
        jamMulaiMenit: Int
    ): ReminderScheduleResult

    suspend fun cancelJobReminders(jobId: Long)
}
