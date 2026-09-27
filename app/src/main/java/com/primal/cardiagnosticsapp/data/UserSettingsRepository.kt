package com.primal.cardiagnosticsapp.data

import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import com.primal.cardiagnosticsapp.ui.theme.ThemeMode
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsRow(
    @SerialName("user_id") val userId: String,
    val theme: String = ThemeMode.SYSTEM.name,
    val language: String = AppLanguage.SYSTEM.name,
    @SerialName("graph_colors") val graphColors: Map<String, Int> = emptyMap()
)

class UserSettingsRepository(
    private val supabase: SupabaseClient = SupabaseProvider.client
) {
    suspend fun getSettings(userId: String): UserSettingsRow? {
        return supabase
            .from("user_settings")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<UserSettingsRow>()
            .firstOrNull()
    }

    suspend fun upsertSettings(row: UserSettingsRow): UserSettingsRow {
        return supabase
            .from("user_settings")
            .upsert(row) {
                select()
            }
            .decodeSingle()
    }

    suspend fun updateTheme(userId: String, theme: ThemeMode) {
        val current = getSettings(userId)
        upsertSettings(
            (current ?: UserSettingsRow(userId = userId)).copy(
                theme = theme.name
            )
        )
    }

    suspend fun updateLanguage(userId: String, language: AppLanguage) {
        val current = getSettings(userId)
        upsertSettings(
            (current ?: UserSettingsRow(userId = userId)).copy(
                language = language.name
            )
        )
    }

    suspend fun updateSingleGraphColor(
        userId: String,
        key: String,
        argb: Int
    ) {
        val current = getSettings(userId) ?: UserSettingsRow(userId = userId)
        val merged = current.graphColors.toMutableMap().apply {
            put(key, argb)
        }
        upsertSettings(current.copy(graphColors = merged))
    }
}