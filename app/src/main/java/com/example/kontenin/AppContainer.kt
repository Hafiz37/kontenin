package com.example.kontenin

import androidx.lifecycle.SavedStateHandle
import com.example.kontenin.contract.*
import com.example.kontenin.demo.*
import com.example.kontenin.presentation.home.HomeViewModel
import com.example.kontenin.presentation.jobform.JobFormViewModel
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock

/**
 * AppContainer untuk Kontenin - DI manual.
 * 
 * Menyediakan instance gateway bersama dan factory ViewModel.
 * Diupdate di FE-08 untuk menambahkan JobFormViewModel factory dengan SavedStateHandle.
 */
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
    
    // ViewModel Factories (FE-07+)
    
    fun createHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            jobGateway = jobGateway,
            clock = clock
        )
    }
    
    // FE-08: JobFormViewModel factory dengan SavedStateHandle
    fun createJobFormViewModel(
        savedStateHandle: SavedStateHandle,
        jobId: Long,
        mode: String
    ): JobFormViewModel {
        return JobFormViewModel(
            jobGateway = jobGateway,
            savedStateHandle = savedStateHandle,
            jobId = jobId,
            mode = mode
        )
    }
}
