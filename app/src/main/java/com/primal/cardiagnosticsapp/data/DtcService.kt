package com.primal.cardiagnosticsapp.data

class DtcService(
    private val dataSource: DtcDataSource
) {

    suspend fun isDuplicateOfLatestScan(
        userId: String,
        vehicleId: String,
        dtcs: List<DtcItem>
    ): Boolean {
        val latest = dataSource
            .getScanHistory(userId, vehicleId)
            .firstOrNull()
            ?: return false

        val latestItems = dataSource.getScanItems(
            userId = userId,
            scanId = latest.id
        )

        val latestCodes = latestItems
            .map { it.code.trim().uppercase() }
            .sorted()

        val newCodes = dtcs
            .map { it.code.trim().uppercase() }
            .sorted()

        return latestCodes == newCodes
    }
}