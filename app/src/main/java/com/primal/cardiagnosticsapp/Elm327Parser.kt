package com.primal.cardiagnosticsapp

import com.primal.cardiagnosticsapp.data.DtcItem
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage

object Elm327Parser {
    data class Snapshot(
        val rpm: Double?,
        val speedKph: Int?,
        val coolantC: Int?,
        val throttlePct: Double?,
        val acceleratorPedalPct: Float?,
        val boostKpa: Double?,
        val boostPsi: Double?,
        val mapKpa: Int?,
        val mafGps: Float?,
        val iatC: Int?,
        val timingAdvanceDeg: Float?,
        val fuelLevelPct: Float?,
        val fuelRateLph: Float?,
        val fuelConsumptionL100: Float?,
        val engineLoadPct: Float?,
        val fuelPressureKpa: Int?,
        val baroKpa: Int?,
        val controlModuleVoltage: Float?
    )

    private fun clean(raw: String) =
        raw.replace("\r","").replace("\n","").replace(" ","").uppercase()

    private fun calculateFuelRateLph(
        mafGps: Float?,
        afr: Float = 14.7f,
        fuelDensityGPerL: Float = 745f
    ): Float? {
        if (mafGps == null || mafGps <= 0f) return null
        return (mafGps * 3600f) / (afr * fuelDensityGPerL)
    }

    private fun calculateFuelConsumptionLPer100(
        speedKph: Int?,
        mafGps: Float?,
        afr: Float = 14.7f,
        fuelDensityGPerL: Float = 745f
    ): Float? {
        if (speedKph == null || speedKph <= 0) return null
        if (mafGps == null || mafGps <= 0f) return null

        val litersPerHour = (mafGps * 3600f) / (afr * fuelDensityGPerL)
        return (litersPerHour / speedKph.toFloat()) * 100f
    }

    fun parseFrame(raw: String): Snapshot? {
        val s = clean(raw)

        var rpm: Double? = null
        var spd: Int? = null
        var clt: Int? = null
        var thr: Double? = null
        var acceleratorPedalPct: Float?
        var mapKpa: Int? = null
        var baroKpa: Int? = null
        var fuelLevelPct: Float? = null
        var mafGps: Float? = null
        var iatC: Int? = null
        var timingAdvanceDeg: Float? = null
        var engineLoadPct: Float? = null
        var fuelPressureKpa: Int? = null
        var controlModuleVoltage: Float? = null

        // RPM: 41 0C AA BB
        val iRpm = s.indexOf("410C")
        if (iRpm >= 0 && s.length >= iRpm + 8) {
            val a = s.substring(iRpm + 4, iRpm + 6).toInt(16)
            val b = s.substring(iRpm + 6, iRpm + 8).toInt(16)
            rpm = ((a * 256) + b) / 4.0
        }

        // Speed: 41 0D AA  (km/h)
        val iSpd = s.indexOf("410D")
        if (iSpd >= 0 && s.length >= iSpd + 6) {
            spd = s.substring(iSpd + 4, iSpd + 6).toInt(16)
        }

        // Coolant: 41 05 AA  (°C = AA - 40)
        val iClt = s.indexOf("4105")
        if (iClt >= 0 && s.length >= iClt + 6) {
            clt = s.substring(iClt + 4, iClt + 6).toInt(16) - 40
        }

        // Engine load: 41 04 AA  (% = AA * 100 / 255)
        val iLoad = s.indexOf("4104")
        if (iLoad >= 0 && s.length >= iLoad + 6) {
            val a = s.substring(iLoad + 4, iLoad + 6).toInt(16)
            engineLoadPct = a * 100f / 255f
        }

        // Fuel pressure: 41 0A AA  (kPa = AA * 3)
        val iFuelPressure = s.indexOf("410A")
        if (iFuelPressure >= 0 && s.length >= iFuelPressure + 6) {
            val a = s.substring(iFuelPressure + 4, iFuelPressure + 6).toInt(16)
            fuelPressureKpa = a * 3
        }

        // Throttle: 41 11 AA  (% = AA * 100 / 255)
        val iThr = s.indexOf("4111")
        if (iThr >= 0 && s.length >= iThr + 6) {
            val a = s.substring(iThr + 4, iThr + 6).toInt(16)
            thr = a * 100.0 / 255.0
        }

        // Accelerator pedal position D/E/F:
        // 41 49 AA, 41 4A AA, 41 4B AA  (% = AA * 100 / 255)
        // These represent accelerator pedal sensor positions, depending on vehicle support.
        fun parsePercentPid(pid: String): Float? {
            val i = s.indexOf(pid)
            if (i < 0 || s.length < i + 6) return null
            val a = s.substring(i + 4, i + 6).toIntOrNull(16) ?: return null
            return a * 100f / 255f
        }

        acceleratorPedalPct =
            parsePercentPid("4149")
                ?: parsePercentPid("414A")
                        ?: parsePercentPid("414B")

        // Timing advance: 41 0E AA  (deg = A / 2 - 64)
        val iTiming = s.indexOf("410E")
        if (iTiming >= 0 && s.length >= iTiming + 6) {
            val a = s.substring(iTiming + 4, iTiming + 6).toInt(16)
            timingAdvanceDeg = (a / 2.0f) - 64f
        }

        // IAT: 41 0F AA  (°C = AA - 40)
        val iIat = s.indexOf("410F")
        if (iIat >= 0 && s.length >= iIat + 6) {
            iatC = s.substring(iIat + 4, iIat + 6).toInt(16) - 40
        }

        // MAF: 41 10 AA BB  (grams/sec = ((A * 256) + B) / 100)
        val iMaf = s.indexOf("4110")
        if (iMaf >= 0 && s.length >= iMaf + 8) {
            val a = s.substring(iMaf + 4, iMaf + 6).toInt(16)
            val b = s.substring(iMaf + 6, iMaf + 8).toInt(16)
            mafGps = (((a * 256) + b) / 100f)
        }

        // Fuel level: 41 2F AA  (% = AA * 100 / 255)
        val iFuel = s.indexOf("412F")
        if (iFuel >= 0 && s.length >= iFuel + 6) {
            val a = s.substring(iFuel + 4, iFuel + 6).toInt(16)
            fuelLevelPct = a * 100f / 255f
        }

        // MAP (manifold absolute pressure): 41 0B AA  (kPa)
        val iMap = s.indexOf("410B")
        if (iMap >= 0 && s.length >= iMap + 6) {
            mapKpa = s.substring(iMap + 4, iMap + 6).toInt(16)
        }

        // BARO (barometric pressure): 41 33 AA  (kPa)
        val iBaro = s.indexOf("4133")
        if (iBaro >= 0 && s.length >= iBaro + 6) {
            baroKpa = s.substring(iBaro + 4, iBaro + 6).toInt(16)
        }

        // Control module voltage: 41 42 AA BB  (V = ((A * 256) + B) / 1000)
        val iVolt = s.indexOf("4142")
        if (iVolt >= 0 && s.length >= iVolt + 8) {
            val a = s.substring(iVolt + 4, iVolt + 6).toInt(16)
            val b = s.substring(iVolt + 6, iVolt + 8).toInt(16)
            controlModuleVoltage = ((a * 256) + b) / 1000f
        }

        // Approximate boost: MAP - BARO (clip at 0)
        val boostKpa = if (mapKpa != null && baroKpa != null) {
            (mapKpa - baroKpa).coerceAtLeast(0).toDouble()
        } else null
        val boostPsi = boostKpa?.div(6.8947)

        val fuelRateLph = calculateFuelRateLph(mafGps)
        val fuelConsumptionL100 = calculateFuelConsumptionLPer100(
            speedKph = spd,
            mafGps = mafGps
        )

        return if (
            rpm != null ||
            spd != null ||
            clt != null ||
            thr != null ||
            acceleratorPedalPct != null ||
            boostKpa != null ||
            mapKpa != null ||
            mafGps != null ||
            iatC != null ||
            timingAdvanceDeg != null ||
            fuelLevelPct != null ||
            engineLoadPct != null ||
            fuelPressureKpa != null ||
            baroKpa != null ||
            controlModuleVoltage != null
        ) {
            Snapshot(
                rpm = rpm,
                speedKph = spd,
                coolantC = clt,
                throttlePct = thr,
                acceleratorPedalPct = acceleratorPedalPct,
                boostKpa = boostKpa,
                boostPsi = boostPsi,
                mapKpa = mapKpa,
                mafGps = mafGps,
                iatC = iatC,
                timingAdvanceDeg = timingAdvanceDeg,
                fuelLevelPct = fuelLevelPct,
                fuelRateLph = fuelRateLph,
                fuelConsumptionL100 = fuelConsumptionL100,
                engineLoadPct = engineLoadPct,
                fuelPressureKpa = fuelPressureKpa,
                baroKpa = baroKpa,
                controlModuleVoltage = controlModuleVoltage
            )
        } else {
            null
        }
    }

    /** Parse DTC response modes:
     * 43 = stored DTCs (request 03)
     * 47 = pending DTCs (request 07)
     * 4A = permanent DTCs (request 0A)
     */
    fun parseDtcs(
        raw: String,
        language: AppLanguage = AppLanguage.ENGLISH,
        responseMode: String = "43"
    ): List<DtcItem> {
        val s = clean(raw)
        val i = s.indexOf(responseMode)
        if (i < 0) return emptyList()

        val bytes = s.substring(i + responseMode.length)
            .chunked(2)
            .mapNotNull { it.toIntOrNull(16) }

        if (bytes.isEmpty()) return emptyList()

        val typeMap = charArrayOf('P', 'C', 'B', 'U')
        val codes = mutableListOf<String>()

        for (pair in bytes.chunked(2)) {
            if (pair.size < 2) break
            val a = pair[0]
            val b = pair[1]

            if (a == 0 && b == 0) continue

            val first = typeMap[(a and 0xC0) shr 6]
            val second = (a and 0x30) shr 4
            val third = a and 0x0F
            val fourth = (b and 0xF0) shr 4
            val fifth = b and 0x0F

            val code = buildString(5) {
                append(first)
                append(second)
                append(third.toString(16).uppercase())
                append(fourth.toString(16).uppercase())
                append(fifth.toString(16).uppercase())
            }

            if (code.length == 5 && code[0] in charArrayOf('P', 'C', 'B', 'U')) {
                codes += code
            }
        }

        return codes.distinct().map { code ->
            val meta = DtcDictionary.meta(code, language)
            DtcItem(
                code = code,
                severity = meta.severity,
                description = meta.description,
                isGeneric = meta.isGeneric,
                recommendation = meta.recommendation,
                likelyCauses = meta.likelyCauses
            )
        }
    }

    /** Parse Mode 09 PID 02 multi-frame VIN (49 02 .. ..) */
    fun tryParseVin(raw: String): String? {
        val normalized = raw
            .replace(">", " ")
            .replace(Regex("""\b[0-9A-F]:"""), " ")
            .replace(Regex("""[^0-9A-Fa-f]"""), "")
            .uppercase()

        val bytes = mutableListOf<Int>()
        var idx = normalized.indexOf("4902")

        while (idx >= 0) {
            var pos = idx + 4

            if (pos + 2 <= normalized.length) pos += 2

            while (pos + 2 <= normalized.length) {
                val hex = normalized.substring(pos, pos + 2)
                val b = hex.toIntOrNull(16) ?: break
                if (b == 0) break

                if (b in 0x20..0x7E) {
                    bytes += b
                }

                pos += 2

                if (pos + 4 <= normalized.length && normalized.substring(pos, pos + 4) == "4902") {
                    break
                }
            }

            idx = normalized.indexOf("4902", idx + 1)
        }

        if (bytes.isEmpty()) return null

        val vin = bytes
            .map { it.toChar() }
            .joinToString("")
            .filter { it.isLetterOrDigit() }
            .take(17)

        return if (vin.length == 17) vin else null
    }

    fun supportedPidSetFrom4100(frame: String): Set<String> {
        val s = frame.replace("\\s|\\r|\\n".toRegex(),"").uppercase()
        val i = s.indexOf("4100")
        if (i < 0) return emptySet()
        val hex = s.substring(i + 4).take(8)
        if (hex.length < 8) return emptySet()
        val bytes = hex.chunked(2).map { it.toInt(16) }
        val out = mutableSetOf<String>()
        for (bit in 0 until 32) {
            val b = bytes[bit/8]
            val mask = 1 shl (7 - (bit % 8))
            if ((b and mask) != 0) {
                val pid = 0x01 + bit
                out += "%02X".format(pid)
            }
        }
        return out
    }
}
