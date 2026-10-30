package com.example.kontenin.contract

sealed interface ExportResult {
    data class Success(val path: String, val jobCount: Int) : ExportResult
    data class Error(val message: String) : ExportResult
}

sealed interface ImportValidationResult {
    data class Valid(val jobCount: Int, val preview: String) : ImportValidationResult
    data class Invalid(val message: String) : ImportValidationResult
}

sealed interface ImportResult {
    data class Success(val importedCount: Int) : ImportResult
    data class Error(val message: String) : ImportResult
}

interface BackupGateway {
    suspend fun exportToJson(): ExportResult
    suspend fun validateImport(filePath: String): ImportValidationResult
    suspend fun confirmImport(filePath: String): ImportResult
}
