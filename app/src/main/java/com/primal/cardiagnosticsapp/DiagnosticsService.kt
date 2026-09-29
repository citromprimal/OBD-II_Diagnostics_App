package com.primal.cardiagnosticsapp

class DiagnosticsService(
    private val obdManager: ObdManager
) {

    suspend fun readStoredDtcs() {
        obdManager.setPollingEnabled(false)
        obdManager.send("03")
    }

    suspend fun readPendingDtcs() {
        obdManager.setPollingEnabled(false)
        obdManager.send("07")
    }

    suspend fun readPermanentDtcs() {
        obdManager.setPollingEnabled(false)
        obdManager.send("0A")
    }

    suspend fun resumePolling() {
        obdManager.setPollingEnabled(true)
    }
}