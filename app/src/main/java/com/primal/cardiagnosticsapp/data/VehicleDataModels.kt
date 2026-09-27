package com.primal.cardiagnosticsapp.data

// ---------- STATIC DATA ----------

data class StaticVehicleData(
    val vin: String? = null,
    val vinInfo: VinInfo? = null,
    val dtcs: List<DtcItem> = emptyList(),
    val pendingDtcs: List<DtcItem> = emptyList(),
    val permanentDtcs: List<DtcItem> = emptyList(),
    val lastDtcScanAt: Long? = null,
    val fuelType: String? = null
)

data class VinInfo(
    val manufacturer: String? = null,
    val country: String? = null,
    val year: Int? = null,
    val wmi: String? = null,
    val region: String? = null,
    val checkDigitValid: Boolean? = null,
    val yearCandidates: List<Int>? = null,
    val isValidFormat: Boolean = false,
    val isYearAmbiguous: Boolean = false,
    val isYearReliable: Boolean = true
)

// ---------- DTC WITH SEVERITY ----------

data class DtcItem(
    val code: String,
    val severity: DtcSeverity,
    val description: String? = null,
    val isGeneric: Boolean? = null,
    val recommendation: String? = null,
    val likelyCauses: List<String> = emptyList()
)

enum class DtcSeverity {
    CRITICAL,
    WARNING,
    INFO
}

// ---------- DYNAMIC DATA ----------

data class DynamicVehicleData(
    val rpm: Double? = null,
    val speedKph: Int? = null,
    val coolantC: Int? = null,
    val throttlePct: Double? = null,
    val acceleratorPedalPct: Float? = null,
    val boostPsi: Double? = null,
    val mapKpa: Int? = null,
    val iatC: Int? = null,
    val timingAdvanceDeg: Float? = null,
    val fuelLevelPct: Float? = null,
    val mafGps: Float? = null,
    val fuelRateLph: Float? = null,
    val fuelConsumptionL100: Float? = null,
    val fuelPressureKpa: Int? = null,
    val engineLoadPct: Float? = null,
    val baroKpa: Int? = null,
    val controlModuleVoltage: Float? = null
)