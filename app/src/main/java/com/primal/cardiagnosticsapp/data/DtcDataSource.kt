package com.primal.cardiagnosticsapp.data

interface DtcDataSource {

    suspend fun getScanHistory(
        userId: String,
        vehicleId: String
    ): List<DtcScanHistoryRow>

    suspend fun getScanItems(
        userId: String,
        scanId: String
    ): List<DtcScanItemHistoryRow>
}