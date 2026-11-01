package com.example.kontenin.demo

import com.example.kontenin.contract.*
import com.example.kontenin.util.AppClock
import com.example.kontenin.util.SystemClock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

/**
 * DemoBackupGateway (FE-17).
 *
 * Mengimplementasikan alur cadangan data simulasi:
 * - Ekspor simulasi dengan nama saran `kontenin-backup-YYYYMMDD.json` (FR-7.1, EC-18).
 * - Validasi file cadangan sebelum konfirmasi (FR-7.2, FR-7.3, EC-16).
 * - Impor dengan penggantian atomik koleksi demo pada DemoJobGateway (FR-7.2).
 * - Rollback dan pembatalan tanpa meninggalkan data parsial (EC-17, EC-18).
 * - Tanpa file nyata, Storage Access Framework (SAF), serializer, atau transaksi Room.
 */
class DemoBackupGateway(
    private val jobGateway: DemoJobGateway,
    var scenario: DemoScenario = DemoScenario.SUCCESS,
    val clock: AppClock = SystemClock()
) : BackupGateway {

    var simulateExportError: Boolean = false
    var simulateExportCancel: Boolean = false
    var simulatedDelayMs: Long = 0L

    fun getSuggestedFileName(): String {
        val dateStr = DateTimeFormatter.ofPattern("yyyyMMdd").format(clock.nowLocalDate())
        return "kontenin-backup-$dateStr.json"
    }

    override suspend fun exportToJson(destinationPath: String?): ExportResult {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        if (simulateExportCancel) {
            return ExportResult.Cancelled
        }

        if (simulateExportError || scenario == DemoScenario.SAVE_ERROR) {
            return ExportResult.Error("Gagal mengekspor data")
        }

        val jobs = jobGateway.observeAllJobs().first()
        val path = destinationPath ?: getSuggestedFileName()
        return ExportResult.Success(
            path = path,
            jobCount = jobs.size
        )
    }

    override suspend fun validateImport(filePath: String): ImportValidationResult {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        if (scenario == DemoScenario.SAVE_ERROR) {
            return ImportValidationResult.Invalid("File cadangan tidak valid")
        }

        val fixture = DemoBackupFixtures.getFixture(filePath)
        if (fixture != null) {
            return if (fixture.isValid) {
                ImportValidationResult.Valid(
                    jobCount = fixture.jobs.size,
                    preview = "${fixture.jobs.size} job dari file cadangan"
                )
            } else {
                ImportValidationResult.Invalid(fixture.errorMessage ?: "File cadangan tidak valid")
            }
        }

        // Cek path yang menandakan file invalid
        if (filePath.contains("corrupt", ignoreCase = true) ||
            filePath.contains("invalid", ignoreCase = true) ||
            filePath.contains("newer", ignoreCase = true) ||
            filePath.contains("v99", ignoreCase = true)
        ) {
            return ImportValidationResult.Invalid("File cadangan tidak valid")
        }

        // Default valid fallback (3 jobs)
        return ImportValidationResult.Valid(
            jobCount = DemoBackupFixtures.valid3Jobs.size,
            preview = "${DemoBackupFixtures.valid3Jobs.size} job dari file cadangan"
        )
    }

    override suspend fun confirmImport(filePath: String): ImportResult {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        val fixture = DemoBackupFixtures.getFixture(filePath)

        if (scenario == DemoScenario.SAVE_ERROR || (fixture != null && fixture.willFailProcess)) {
            // Rollback simulasi: data demo lama tidak diubah sama sekali (EC-17)
            return ImportResult.Error("Gagal mengimpor data")
        }

        if (fixture != null && !fixture.isValid) {
            return ImportResult.Error(fixture.errorMessage ?: "File cadangan tidak valid")
        }

        val jobsToImport = fixture?.jobs ?: DemoBackupFixtures.valid3Jobs

        // Terapkan penggantian atomik hanya pada koleksi demo bersama (FE-17, FR-7.2)
        jobGateway.replaceAllJobs(jobsToImport)

        return ImportResult.Success(importedCount = jobsToImport.size)
    }
}
