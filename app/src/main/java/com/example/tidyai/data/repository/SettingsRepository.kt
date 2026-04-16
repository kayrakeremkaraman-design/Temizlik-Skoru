package com.example.tidyai.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

enum class AppTheme(val displayName: String, val emoji: String) {
    DARK("Koyu Tema", "🌑"),
    MINT("Mint Yeşili", "🌿"),
    OCEAN("Okyanus Mavisi", "🌊"),
    SUNSET("Gün Batımı", "🌅"),
    PURPLE("Mor Gece", "🔮")
}

interface SettingsRepository {
    val apiKey: Flow<String?>
    val appTheme: Flow<AppTheme>
    val hasSeenOnboarding: Flow<Boolean>
    val queryCount: Flow<Int>
    suspend fun saveApiKey(key: String)
    suspend fun saveTheme(theme: AppTheme)
    suspend fun markOnboardingSeen()
    suspend fun incrementQueryCount()
    suspend fun decrementQueryCount()
    suspend fun resetQueryCount()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val API_KEY           = stringPreferencesKey("api_key")
        val APP_THEME         = stringPreferencesKey("app_theme")
        val ONBOARDING_SEEN   = booleanPreferencesKey("onboarding_seen")
        val QUERY_COUNT       = intPreferencesKey("query_count")
    }

    override val apiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.API_KEY]
    }

    override val appTheme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.APP_THEME] ?: AppTheme.DARK.name
        AppTheme.entries.find { it.name == name } ?: AppTheme.DARK
    }

    override val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_SEEN] ?: false
    }

    override val queryCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.QUERY_COUNT] ?: 0
    }

    override suspend fun saveApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.API_KEY] = key
        }
    }

    override suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme.name
        }
    }

    override suspend fun markOnboardingSeen() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_SEEN] = true
        }
    }

    override suspend fun incrementQueryCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.QUERY_COUNT] ?: 0
            preferences[PreferencesKeys.QUERY_COUNT] = current + 1
        }
    }

    override suspend fun decrementQueryCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.QUERY_COUNT] ?: 0
            preferences[PreferencesKeys.QUERY_COUNT] = maxOf(0, current - 1)
        }
    }

    override suspend fun resetQueryCount() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUERY_COUNT] = 0
        }
    }
}
