package com.primal.cardiagnosticsapp

import android.content.Context
import com.primal.cardiagnosticsapp.ui.theme.AppLanguage
import com.primal.cardiagnosticsapp.ui.theme.ThemeMode

private const val PREFS = "app_prefs"
private const val KEY_LANG = "app_language"
private const val KEY_THEME = "app_theme"

fun saveLanguage(context: Context, lang: AppLanguage) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_LANG, lang.name)
        .apply()
}

fun loadLanguage(context: Context): AppLanguage {
    val raw = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(KEY_LANG, AppLanguage.SYSTEM.name)

    return runCatching { AppLanguage.valueOf(raw!!) }
        .getOrDefault(AppLanguage.SYSTEM)
}

fun saveThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_THEME, mode.name)
        .apply()
}

fun loadThemeMode(context: Context): ThemeMode {
    val raw = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(KEY_THEME, ThemeMode.SYSTEM.name)

    return runCatching { ThemeMode.valueOf(raw!!) }
        .getOrDefault(ThemeMode.SYSTEM)
}
