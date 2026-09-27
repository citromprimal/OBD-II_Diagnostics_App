package com.primal.cardiagnosticsapp.data

fun calculateFuelRateLph(
    mafGps: Float?,
    afr: Float = 14.7f,
    fuelDensityGPerL: Float = 745f
): Float? {
    if (mafGps == null || mafGps <= 0f) return null
    return (mafGps * 3600f) / (afr * fuelDensityGPerL)
}

fun calculateFuelConsumptionLPer100(
    speedKph: Int?,
    mafGps: Float?,
    afr: Float = 14.7f,
    fuelDensityGPerL: Float = 745f
): Float? {
    if (speedKph == null || speedKph <= 0) return null
    if (mafGps == null || mafGps <= 0f) return null

    val litersPerHour = calculateFuelRateLph(
        mafGps = mafGps,
        afr = afr,
        fuelDensityGPerL = fuelDensityGPerL
    ) ?: return null

    return (litersPerHour / speedKph.toFloat()) * 100f
}