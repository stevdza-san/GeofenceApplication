package com.example.geofenceapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.geofenceapp.util.Constants.PREFERENCE_FIRST_LAUNCH
import com.example.geofenceapp.util.Constants.PREFERENCE_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCE_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKey {
        val firstLaunch = booleanPreferencesKey(PREFERENCE_FIRST_LAUNCH)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveFirstLaunch(firstLaunch: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKey.firstLaunch] = firstLaunch
        }
    }

    val readFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val firstLaunch = preference[PreferenceKey.firstLaunch] ?: true
            firstLaunch
        }

}