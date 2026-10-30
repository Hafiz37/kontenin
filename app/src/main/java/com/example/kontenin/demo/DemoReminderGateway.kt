package com.example.kontenin.demo

import com.example.kontenin.contract.*

class DemoReminderGateway(
    private val scenario: DemoScenario = DemoScenario.SUCCESS
) : ReminderGateway {
    
    private var currentPermissionStatus: PermissionStatus = PermissionStatus.NotRequested
    
    override suspend fun getPermissionStatus(): PermissionStatus {
        return currentPermissionStatus
    }
    
    override suspend fun requestPermission(): RequestPermissionResult {
        return when (scenario) {
            DemoScenario.SUCCESS -> {
                currentPermissionStatus = PermissionStatus.Granted
                RequestPermissionResult.Granted
            }
            DemoScenario.SAVE_ERROR -> {
                currentPermissionStatus = PermissionStatus.Denied
                RequestPermissionResult.Denied
            }
            else -> RequestPermissionResult.Cancelled
        }
    }
    
    override suspend fun testReminder(): TestReminderResult {
        return when (currentPermissionStatus) {
            PermissionStatus.Granted -> TestReminderResult.Success
            else -> TestReminderResult.Error("Izin notifikasi belum diberikan")
        }
    }
}
