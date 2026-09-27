package com.primal.cardiagnosticsapp

import kotlinx.coroutines.flow.StateFlow

data class DeviceItem(val name: String, val address: String)
typealias VehicleSnapshot = Elm327Parser.Snapshot

interface ObdManager {
    val devices: StateFlow<List<DeviceItem>>

    fun startScan(onError: (String) -> Unit, onStarted: () -> Unit)
    fun stopScan()

    suspend fun connect(
        device: DeviceItem,
        onLine: (String) -> Unit,
        onData: (VehicleSnapshot) -> Unit,
        onStatus: (String) -> Unit
    )

    /** Queue a single ELM/OBD command (e.g., "0902", "03", "04"). */
    suspend fun send(cmd: String)

    /** Enable/disable background PID polling (e.g., pause during VIN/DTC). */
    suspend fun setPollingEnabled(enabled: Boolean)

    suspend fun disconnect()
}
