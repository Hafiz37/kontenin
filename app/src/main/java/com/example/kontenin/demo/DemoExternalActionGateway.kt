package com.example.kontenin.demo

import com.example.kontenin.contract.*

class DemoExternalActionGateway(
    var scenario: DemoScenario = DemoScenario.SUCCESS
) : ExternalActionGateway {
    
    val openedMapsEvents = mutableListOf<String>()
    val lastOpenedMapsAddress: String?
        get() = openedMapsEvents.lastOrNull()
        
    var simulateOpenMapsError: Boolean = false

    override suspend fun openMaps(address: String): OpenMapsResult {
        if (simulateOpenMapsError || scenario == DemoScenario.SAVE_ERROR) {
            return OpenMapsResult.Error("Tidak ada aplikasi untuk membuka link ini")
        }
        openedMapsEvents.add(address)
        return OpenMapsResult.Success
    }
    
    override suspend fun openSystemSettings(): OpenSystemSettingsResult {
        if (scenario == DemoScenario.SAVE_ERROR) {
            return OpenSystemSettingsResult.Error("Simulasi gagal buka Settings")
        }
        return OpenSystemSettingsResult.Success
    }
}
