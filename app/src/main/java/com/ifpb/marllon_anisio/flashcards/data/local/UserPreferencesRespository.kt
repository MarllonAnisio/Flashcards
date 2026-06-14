package com.ifpb.marllon_anisio.flashcards.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("app_theme")

    // Retorna "SYSTEM" por padrão se nada estiver salvo
    val appTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "SYSTEM"
    }

    suspend fun saveThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
}