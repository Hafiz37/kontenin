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
        if (externalActionGateway is DemoExternalActionGateway) {
            externalActionGateway.scenario = scenario
        }
    }
    
    // ViewModel Factories (FE-07+)
    
    fun createHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            jobGateway = jobGateway,
            clock = clock
        )
    }
    
    // FE-08, FE-16: JobFormViewModel factory dengan SavedStateHandle & ReminderGateway
    fun createJobFormViewModel(
        savedStateHandle: SavedStateHandle,
        jobId: Long,
        mode: String
    ): JobFormViewModel {
        return JobFormViewModel(
            jobGateway = jobGateway,
            savedStateHandle = savedStateHandle,
            jobId = jobId,
            mode = mode,
            reminderGateway = reminderGateway
        )
    }

    // FE-13: JobDetailViewModel factory
    fun createJobDetailViewModel(jobId: Long): com.example.kontenin.presentation.jobdetail.JobDetailViewModel {
        return com.example.kontenin.presentation.jobdetail.JobDetailViewModel(
            jobGateway = jobGateway,
            externalActionGateway = externalActionGateway,
            jobId = jobId
        )
    }

    // FE-15: RecapViewModel factory
    fun createRecapViewModel(): com.example.kontenin.presentation.recap.RecapViewModel {
        return com.example.kontenin.presentation.recap.RecapViewModel(
            jobGateway = jobGateway,
            clock = clock
        )
    }

    // FE-16: SettingsViewModel factory
    fun createSettingsViewModel(): com.example.kontenin.presentation.settings.SettingsViewModel {
        return com.example.kontenin.presentation.settings.SettingsViewModel(
            reminderGateway = reminderGateway,
            externalActionGateway = externalActionGateway
        )
    }
}
