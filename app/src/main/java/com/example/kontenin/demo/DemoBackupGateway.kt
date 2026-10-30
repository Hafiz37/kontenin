package com.example.kontenin.demo

import com.example.kontenin.contract.*
import com.example.kontenin.model.Job
import kotlinx.coroutines.flow.first

class DemoBackupGateway(
    private val jobGateway: DemoJobGateway,
    private val scenario: DemoScenario = DemoScenario.SUCCESS
) : BackupGateway {
    
    override suspend fun exportToJson(): ExportResult {
        return when (scenario) {
            DemoScenario.SAVE_ERROR -> ExportResult.Error("Simulasi gagal ekspor")
            else -> {
                val jobs = jobGateway.observeAllJobs().first()
                ExportResult.Success(
                    path = "/sdcard/Download/kontenin_backup_demo.json",
                    jobCount = jobs.size
                )
            }
        }
    }
    
    override suspend fun validateImport(filePath: String): ImportValidationResult {
        return when (scenario) {
            DemoScenario.SAVE_ERROR -> ImportValidationResult.Invalid("File tidak valid")
            else -> ImportValidationResult.Valid(
                jobCount = 5,
                preview = "5 job dari file demo:\n- Budi Santoso (Pernikahan)\n- Siti Rahayu (Ulang Tahun)\n- ..."
            )
        }
    }
    
    override suspend fun confirmImport(filePath: String): ImportResult {
        return when (scenario) {
            DemoScenario.SAVE_ERROR -> ImportResult.Error("Simulasi gagal impor")
            else -> ImportResult.Success(importedCount = 5)
        }
    }
}
