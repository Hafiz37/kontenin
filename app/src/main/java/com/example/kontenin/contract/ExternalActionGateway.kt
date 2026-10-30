package com.example.kontenin.contract

sealed interface OpenMapsResult {
    data object Success : OpenMapsResult
    data class Error(val message: String) : OpenMapsResult
}

sealed interface OpenSystemSettingsResult {
    data object Success : OpenSystemSettingsResult
    data class Error(val message: String) : OpenSystemSettingsResult
}

interface ExternalActionGateway {
    suspend fun openMaps(address: String): OpenMapsResult
    suspend fun openSystemSettings(): OpenSystemSettingsResult
}
