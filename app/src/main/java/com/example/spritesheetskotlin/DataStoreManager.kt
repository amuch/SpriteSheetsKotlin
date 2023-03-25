package com.example.spritesheetskotlin

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.spritesheetskotlin.dialog.NAME_DATA_STORE
import kotlinx.coroutines.flow.first

class DataStoreManager(context: Context) {
    private val dataStore = context.createDataStore(NAME_DATA_STORE)

    companion object {
        val dimension = preferencesKey<Int>(PREFERENCE_DIMENSION)
        val resolution = preferencesKey<Int>(PREFERENCE_RESOLUTION)
    }

    suspend fun saveToDataStore(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings->
            settings[dataStoreKey] = value
        }
        println("Saved $value to $key in Data Store")
    }

    suspend fun readFromDataStore(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}