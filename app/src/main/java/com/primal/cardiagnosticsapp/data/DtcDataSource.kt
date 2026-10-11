package com.primal.cardiagnosticsapp.data

interface DtcDataSource {

    suspend fun insertScan(
        userId: String,
        vehicleId: String,
        dtcs: List<DtcItem>
    ): String

    suspend fun getScanHistory(
        userId: String,
        vehicleId: String
    ): List<DtcScanHistoryRow>

    suspend fun getScanItems(
        userId: String,
        scanId: String
    ): List<DtcScanItemHistoryRow>
}