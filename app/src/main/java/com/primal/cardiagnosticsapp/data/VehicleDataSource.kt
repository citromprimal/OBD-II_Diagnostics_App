package com.primal.cardiagnosticsapp.data

interface VehicleDataSource {

    suspend fun insertVehicle(
        userId: String,
        vin: String,
        manufacturer: String?,
        country: String?,
        year: Int?,
        wmi: String?,
        region: String?,
        checkDigitValid: Boolean?,
        yearCandidates: List<Int>?,
        isValidFormat: Boolean,
        isYearAmbiguous: Boolean
    ): VehicleRow
}