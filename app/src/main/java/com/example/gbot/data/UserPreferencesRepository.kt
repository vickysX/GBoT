package com.example.gbot.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    val dataStore : DataStore<Preferences>
) {
    val isGiuliaTheme : Flow<Boolean> = dataStore.data
        .catch {exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_GIULIA_THEME] ?: true
        }

    private companion object {
        val IS_GIULIA_THEME = booleanPreferencesKey("is_giulia_theme")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveThemePreference(isGiuliaTheme : Boolean) {
        dataStore.edit {preferences ->
            preferences[IS_GIULIA_THEME] = isGiuliaTheme
        }
    }
}