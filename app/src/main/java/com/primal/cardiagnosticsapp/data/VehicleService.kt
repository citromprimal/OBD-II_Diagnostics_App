package com.primal.cardiagnosticsapp.data

class VehicleService(
    private val dataSource: VehicleDataSource
) {

    suspend fun saveVehicle(
        userId: String,
        vin: String,
        info: VinInfo
    ): VehicleRow {
        return dataSource.insertVehicle(
            userId = userId,
            vin = vin,
            manufacturer = info.manufacturer,
            country = info.country,
            year = info.year,
            wmi = info.wmi,
            region = info.region,
            checkDigitValid = info.checkDigitValid,
            yearCandidates = info.yearCandidates,
            isValidFormat = info.isValidFormat,
            isYearAmbiguous = info.isYearAmbiguous
        )
    }
}