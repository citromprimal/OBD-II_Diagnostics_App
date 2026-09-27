package com.primal.cardiagnosticsapp

import com.primal.cardiagnosticsapp.data.VinInfo

object VinDecoder {

    data class DecodedVinDetails(
        val rawVin: String,
        val isValidFormat: Boolean,
        val checkDigitValid: Boolean?,
        val wmi: String,
        val vds: String,
        val vis: String,
        val region: String,
        val manufacturer: String?,
        val country: String,
        val year: Int?,
        val yearCandidates: List<Int>?,
        val isYearAmbiguous: Boolean,
        val isYearReliable: Boolean
    )

    private data class WmiInfo(
        val manufacturer: String,
        val country: String
    )

    private val regionMap = mapOf(
        '1' to "usa",
        '4' to "usa",
        '5' to "usa",
        '2' to "canada",
        '3' to "mexico",
        '6' to "australia",
        '8' to "argentina_chile",
        '9' to "brazil",
        'J' to "japan",
        'K' to "korea",
        'L' to "china",
        'M' to "india_thailand",
        'S' to "uk",
        'T' to "europe_mixed_t",
        'V' to "europe_mixed_v",
        'W' to "germany",
        'X' to "russia",
        'Y' to "europe_mixed_y",
        'Z' to "italy"
    )

    /**
     * VIN year code repeats every 30 years.
     * We keep all plausible values and then pick the most likely one.
     */
    private val yearCodeMap = mapOf(
        'A' to listOf(1980, 2010),
        'B' to listOf(1981, 2011),
        'C' to listOf(1982, 2012),
        'D' to listOf(1983, 2013),
        'E' to listOf(1984, 2014),
        'F' to listOf(1985, 2015),
        'G' to listOf(1986, 2016),
        'H' to listOf(1987, 2017),
        'J' to listOf(1988, 2018),
        'K' to listOf(1989, 2019),
        'L' to listOf(1990, 2020),
        'M' to listOf(1991, 2021),
        'N' to listOf(1992, 2022),
        'P' to listOf(1993, 2023),
        'R' to listOf(1994, 2024),
        'S' to listOf(1995, 2025),
        'T' to listOf(1996, 2026),
        'V' to listOf(1997, 2027),
        'W' to listOf(1998, 2028),
        'X' to listOf(1999, 2029),
        'Y' to listOf(2000, 2030),
        '1' to listOf(2001),
        '2' to listOf(2002),
        '3' to listOf(2003),
        '4' to listOf(2004),
        '5' to listOf(2005),
        '6' to listOf(2006),
        '7' to listOf(2007),
        '8' to listOf(2008),
        '9' to listOf(2009)
    )

    private val wmiMap = mapOf(
        // USA / NA
        "1HG" to WmiInfo("Honda", "usa"),
        "1FA" to WmiInfo("Ford", "usa"),
        "1C4" to WmiInfo("Chrysler/Jeep", "usa"),
        "1G1" to WmiInfo("Chevrolet", "usa"),
        "1GC" to WmiInfo("Chevrolet", "usa"),
        "1N4" to WmiInfo("Nissan", "usa"),
        "2HG" to WmiInfo("Honda", "canada"),
        "2T1" to WmiInfo("Toyota", "canada"),
        "3VW" to WmiInfo("Volkswagen", "mexico"),
        "3N1" to WmiInfo("Nissan", "mexico"),

        // Japan
        "JHM" to WmiInfo("Honda", "japan"),
        "JHL" to WmiInfo("Honda", "japan"),
        "JF1" to WmiInfo("Subaru", "japan"),
        "JF2" to WmiInfo("Subaru", "japan"),
        "JM1" to WmiInfo("Mazda", "japan"),
        "JN1" to WmiInfo("Nissan", "japan"),
        "JN8" to WmiInfo("Nissan", "japan"),
        "JS1" to WmiInfo("Suzuki", "japan"),
        "JT1" to WmiInfo("Toyota", "japan"),
        "JT2" to WmiInfo("Toyota", "japan"),
        "JT3" to WmiInfo("Toyota", "japan"),
        "JTD" to WmiInfo("Toyota", "japan"),

        // Korea
        "KMH" to WmiInfo("Hyundai", "korea"),
        "KNA" to WmiInfo("Kia", "korea"),
        "KNM" to WmiInfo("Kia", "korea"),

        // Germany
        "WAU" to WmiInfo("Audi", "germany"),
        "WBA" to WmiInfo("BMW", "germany"),
        "WBS" to WmiInfo("BMW M", "germany"),
        "WDB" to WmiInfo("Mercedes-Benz", "germany"),
        "WDC" to WmiInfo("Mercedes-Benz", "germany"),
        "WVW" to WmiInfo("Volkswagen", "germany"),
        "WV1" to WmiInfo("Volkswagen", "germany"),
        "WP0" to WmiInfo("Porsche", "germany"),
        "W0L" to WmiInfo("Opel", "germany"),

        // Czech / Slovakia region
        "TMB" to WmiInfo("Skoda", "czech_republic"),

        // UK
        "SAL" to WmiInfo("Land Rover", "uk"),
        "SCC" to WmiInfo("Lotus", "uk"),
        "SAJ" to WmiInfo("Jaguar", "uk"),

        // France / Spain
        "VF1" to WmiInfo("Renault", "france"),
        "VF3" to WmiInfo("Peugeot", "france"),
        "VF7" to WmiInfo("Citroen", "france"),
        "VSS" to WmiInfo("SEAT", "spain"),

        // Sweden
        "YS3" to WmiInfo("Saab", "sweden"),
        "YV1" to WmiInfo("Volvo", "sweden"),
        "YV4" to WmiInfo("Volvo", "sweden"),

        // Italy
        "ZFA" to WmiInfo("Fiat", "italy"),
        "ZFF" to WmiInfo("Ferrari", "italy"),

        // China
        "LVS" to WmiInfo("Ford", "china"),
        "LSV" to WmiInfo("SAIC Volkswagen", "china")
    )

    private val transliterationMap = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3, 'D' to 4, 'E' to 5, 'F' to 6, 'G' to 7, 'H' to 8,
        'J' to 1, 'K' to 2, 'L' to 3, 'M' to 4, 'N' to 5, 'P' to 7, 'R' to 9,
        'S' to 2, 'T' to 3, 'U' to 4, 'V' to 5, 'W' to 6, 'X' to 7, 'Y' to 8, 'Z' to 9,
        '0' to 0, '1' to 1, '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7, '8' to 8, '9' to 9
    )

    private val vinWeights = listOf(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2)

    private fun normalize(vin: String?): String? {
        val clean = vin?.trim()?.uppercase() ?: return null
        return if (clean.isBlank()) null else clean
    }

    fun isValidVinFormat(vin: String?): Boolean {
        val v = normalize(vin) ?: return false
        if (v.length != 17) return false
        if (v.any { it == 'I' || it == 'O' || it == 'Q' }) return false
        return v.all { it.isDigit() || it in 'A'..'Z' }
    }

    private fun isUnusedCheckDigit(vin: String): Boolean {
        if (vin.length < 9) return false
        return vin[8] == 'Z'
    }

    /**
     * VIN check digit is mostly relevant for North America.
     * Returns:
     * - true/false when format supports validation
     * - null when VIN format is not valid enough to validate
     * - null also when the manufacturer appears not to use the check digit
     */
    fun isCheckDigitValid(vin: String?): Boolean? {
        val v = normalize(vin) ?: return null
        if (!isValidVinFormat(v)) return null
        if (v.length != 17) return null

        if (isUnusedCheckDigit(v)) return null

        val sum = v.mapIndexed { index, ch ->
            val value = transliterationMap[ch] ?: return false
            value * vinWeights[index]
        }.sum()

        val remainder = sum % 11
        val expected = if (remainder == 10) 'X' else ('0' + remainder)
        return v[8] == expected
    }

    private fun isYearCodeReliable(vin: String, wmi: String): Boolean {
        val unreliableWmis = setOf("VF1")
        return wmi !in unreliableWmis
    }

    private fun pickMostLikelyYear(candidates: List<Int>?): Pair<Int?, List<Int>?> {
        if (candidates.isNullOrEmpty()) return null to null

        val currentYear = java.time.Year.now().value

        val realisticCandidates = candidates.filter { it <= currentYear + 1 }

        val preferred = realisticCandidates.maxOrNull()
            ?: candidates.maxOrNull()

        return preferred to if (candidates.size > 1) candidates else null
    }

    fun decodeDetails(vin: String?): DecodedVinDetails? {
        val vinUp = normalize(vin) ?: return null
        if (vinUp.length < 10) return null

        val formatValid = isValidVinFormat(vinUp)
        val checkDigitValid = isCheckDigitValid(vinUp)

        val wmi = vinUp.take(3)
        val vds = vinUp.drop(3).take(6)
        val vis = if (vinUp.length >= 9) vinUp.drop(9) else ""

        val region = regionMap[vinUp[0]] ?: "Unknown"
        val wmiInfo = wmiMap[wmi]

        val yearReliable = isYearCodeReliable(vinUp, wmi)
        val (decodedYear, decodedYearCandidates) = pickMostLikelyYear(yearCodeMap[vinUp[9]])

        val year = if (yearReliable) decodedYear else null
        val yearCandidates = if (yearReliable) decodedYearCandidates else null
        val isYearAmbiguous = yearReliable && (yearCodeMap[vinUp[9]]?.size ?: 0) > 1

        return DecodedVinDetails(
            rawVin = vinUp,
            isValidFormat = formatValid,
            checkDigitValid = checkDigitValid,
            wmi = wmi,
            vds = vds,
            vis = vis,
            region = region,
            manufacturer = wmiInfo?.manufacturer,
            country = wmiInfo?.country ?: region,
            year = year,
            yearCandidates = yearCandidates,
            isYearAmbiguous = isYearAmbiguous,
            isYearReliable = yearReliable
        )
    }

    fun decodeInfo(vin: String?): VinInfo? {
        val details = decodeDetails(vin) ?: return null

        return VinInfo(
            manufacturer = details.manufacturer,
            country = details.country,
            year = details.year,
            wmi = details.wmi,
            region = details.region,
            checkDigitValid = details.checkDigitValid,
            yearCandidates = details.yearCandidates,
            isValidFormat = details.isValidFormat,
            isYearAmbiguous = details.isYearAmbiguous,
            isYearReliable = details.isYearReliable
        )
    }
}