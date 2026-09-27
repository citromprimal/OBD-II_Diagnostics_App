package com.primal.cardiagnosticsapp.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class VehicleRow(
    val id: String? = null,
    val user_id: String,
    val vin: String,
    val manufacturer: String? = null,
    val country: String? = null,
    val year: Int? = null,
    val wmi: String? = null,
    val region: String? = null,
    val check_digit_valid: Boolean? = null,
    val year_candidates: List<Int>? = null,
    val is_valid_format: Boolean = false,
    val is_year_ambiguous: Boolean = false
)

class VehicleRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client
) {
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
    ): VehicleRow {
        val row = VehicleRow(
            user_id = userId,
            vin = vin,
            manufacturer = manufacturer,
            country = country,
            year = year,
            wmi = wmi,
            region = region,
            check_digit_valid = checkDigitValid,
            year_candidates = yearCandidates,
            is_valid_format = isValidFormat,
            is_year_ambiguous = isYearAmbiguous
        )

        return supabase
            .from("vehicles")
            .insert(row) {
                select()
            }
            .decodeSingle<VehicleRow>()
    }

    suspend fun getVehicleByVin(userId: String, vin: String): VehicleRow? {
        return supabase
            .from("vehicles")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("vin", vin)
                }
            }
            .decodeList<VehicleRow>()
            .firstOrNull()
    }

    suspend fun findOrCreateVehicle(
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
    ): VehicleRow {
        val existing = getVehicleByVin(userId, vin)
        if (existing != null) return existing

        return insertVehicle(
            userId = userId,
            vin = vin,
            manufacturer = manufacturer,
            country = country,
            year = year,
            wmi = wmi,
            region = region,
            checkDigitValid = checkDigitValid,
            yearCandidates = yearCandidates,
            isValidFormat = isValidFormat,
            isYearAmbiguous = isYearAmbiguous
        )
    }

    suspend fun getVehiclesForUser(userId: String): List<VehicleRow> {
        return supabase
            .from("vehicles")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<VehicleRow>()
            .sortedByDescending { it.vin }
    }

    suspend fun deleteVehicle(vehicleId: String) {
        supabase
            .from("vehicles")
            .delete {
                filter {
                    eq("id", vehicleId)
                }
            }
    }
}