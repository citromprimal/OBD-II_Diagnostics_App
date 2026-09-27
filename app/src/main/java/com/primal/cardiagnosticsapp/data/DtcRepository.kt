package com.primal.cardiagnosticsapp.data

import com.primal.cardiagnosticsapp.DtcDictionary
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtcScanRow(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("vehicle_id") val vehicleId: String,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("critical_count") val criticalCount: Int,
    @SerialName("warning_count") val warningCount: Int,
    @SerialName("info_count") val infoCount: Int
)

@Serializable
data class DtcScanItemRow(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("scan_id") val scanId: String,
    val code: String,
    val severity: String,
    val description: String? = null,
    @SerialName("is_generic") val isGeneric: Boolean? = null,
    val recommendation: String? = null,
    @SerialName("likely_causes") val likelyCauses: String? = null
)

@Serializable
data class DtcScanHistoryRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("vehicle_id") val vehicleId: String,
    @SerialName("scanned_at") val scannedAt: String,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("critical_count") val criticalCount: Int,
    @SerialName("warning_count") val warningCount: Int,
    @SerialName("info_count") val infoCount: Int
)

@Serializable
data class DtcScanItemHistoryRow(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("scan_id") val scanId: String,
    val code: String,
    val severity: String,
    val description: String? = null,
    @SerialName("is_generic") val isGeneric: Boolean? = null,
    val recommendation: String? = null,
    @SerialName("likely_causes") val likelyCauses: String? = null
)

class DtcRepository(
    private val supabase: io.github.jan.supabase.SupabaseClient = SupabaseProvider.client
) {
    suspend fun insertScan(
        userId: String,
        vehicleId: String,
        dtcs: List<DtcItem>
    ): String {
        val critical = dtcs.count { it.severity.name == "CRITICAL" }
        val warning = dtcs.count { it.severity.name == "WARNING" }
        val info = dtcs.count { it.severity.name == "INFO" }

        val scan = DtcScanRow(
            userId = userId,
            vehicleId = vehicleId,
            totalCount = dtcs.size,
            criticalCount = critical,
            warningCount = warning,
            infoCount = info
        )

        val inserted = supabase
            .from("dtc_scans")
            .insert(scan) {
                select()
            }
            .decodeSingle<DtcScanRow>()

        return inserted.id ?: error("Inserted DTC scan has no id")
    }

    suspend fun insertScanItems(
        userId: String,
        scanId: String,
        dtcs: List<DtcItem>,
        language: AppLanguage
    ) {
        val rows = dtcs.map {
            val meta = DtcDictionary.meta(it.code, language)

            DtcScanItemRow(
                userId = userId,
                scanId = scanId,
                code = it.code,
                severity = it.severity.name,
                description = meta.description,
                isGeneric = meta.isGeneric,
                recommendation = meta.recommendation,
                likelyCauses = meta.likelyCauses.joinToString(" | ")
            )
        }

        if (rows.isNotEmpty()) {
            supabase.from("dtc_scan_items").insert(rows)
        }
    }

    suspend fun getScanHistory(
        userId: String,
        vehicleId: String
    ): List<DtcScanHistoryRow> {
        return supabase
            .from("dtc_scans")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("vehicle_id", vehicleId)
                }
            }
            .decodeList<DtcScanHistoryRow>()
            .sortedByDescending { it.scannedAt }
    }

    suspend fun getScanItems(
        userId: String,
        scanId: String
    ): List<DtcScanItemHistoryRow> {
        return supabase
            .from("dtc_scan_items")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("scan_id", scanId)
                }
            }
            .decodeList<DtcScanItemHistoryRow>()
    }

    suspend fun getLatestScan(
        userId: String,
        vehicleId: String
    ): DtcScanHistoryRow? {
        return getScanHistory(userId, vehicleId).firstOrNull()
    }

    suspend fun isDuplicateOfLatestScan(
        userId: String,
        vehicleId: String,
        dtcs: List<DtcItem>
    ): Boolean {
        val latest = getLatestScan(userId, vehicleId) ?: return false
        val latestItems = getScanItems(userId, latest.id)

        val latestCodes = latestItems
            .map { it.code.trim().uppercase() }
            .sorted()

        val newCodes = dtcs
            .map { it.code.trim().uppercase() }
            .sorted()

        return latestCodes == newCodes
    }

    suspend fun deleteScan(scanId: String) {
        supabase
            .from("dtc_scans")
            .delete {
                filter {
                    eq("id", scanId)
                }
            }
    }
}