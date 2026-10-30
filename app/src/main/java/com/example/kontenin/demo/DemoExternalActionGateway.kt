package com.example.kontenin.demo

import com.example.kontenin.contract.*

class DemoExternalActionGateway(
    private val scenario: DemoScenario = DemoScenario.SUCCESS
) : ExternalActionGateway {
    
    override suspend fun openMaps(address: String): OpenMapsResult {
        return when (scenario) {
            DemoScenario.SAVE_ERROR -> OpenMapsResult.Error("Simulasi gagal buka Maps")
            else -> OpenMapsResult.Success
        }
    }
    
    override suspend fun openSystemSettings(): OpenSystemSettingsResult {
        return when (scenario) {
            DemoScenario.SAVE_ERROR -> OpenSystemSettingsResult.Error("Simulasi gagal buka Settings")
            else -> OpenSystemSettingsResult.Success
        }
    }
}
