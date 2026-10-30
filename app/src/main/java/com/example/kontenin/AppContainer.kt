package com.example.kontenin

import com.example.kontenin.contract.*
import com.example.kontenin.demo.*
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock

class AppContainer(
    val clock: AppClock = SystemClock(),
    initialScenario: DemoScenario = DemoScenario.SUCCESS
) {
    val jobGateway: DemoJobGateway = DemoJobGateway(clock, initialScenario)
    val reminderGateway: ReminderGateway = DemoReminderGateway(initialScenario)
    val backupGateway: BackupGateway = DemoBackupGateway(jobGateway, initialScenario)
    val externalActionGateway: ExternalActionGateway = DemoExternalActionGateway(initialScenario)
    
    fun resetToSeed() {
        jobGateway.resetToSeed()
    }
    
    fun setScenario(scenario: DemoScenario) {
        jobGateway.setScenario(scenario)
    }
}
