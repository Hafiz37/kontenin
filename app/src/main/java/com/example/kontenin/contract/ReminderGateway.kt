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
    data object Success : TestReminderResult
    data class Error(val message: String) : TestReminderResult
}

interface ReminderGateway {
    suspend fun getPermissionStatus(): PermissionStatus
    suspend fun requestPermission(): RequestPermissionResult
    suspend fun testReminder(): TestReminderResult
}
