package com.example.spritesheetskotlin

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.spritesheetskotlin.dialog.NAME_DATA_STORE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class DataStoreManager(activity: Activity) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = activity.createDataStore(name = NAME_DATA_STORE)
    }

    private suspend fun saveToDataStore(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings->
            settings[dataStoreKey] = value
        }
        println("Saved $value to $key in Data Store")
    }

    private suspend fun readFromDataStore(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    fun savePreference(key: String, value: String) {
        coroutineScope.launch {
            saveToDataStore(key, value)
        }
    }

    fun readPreference(key: String): String? {
        var storedValue: String? = null
        val value = coroutineScope.async {
            readFromDataStore(key)
        }
        value.invokeOnCompletion {
            if(null == it) {
                storedValue = value.toString()
            }
        }
        return storedValue
    }
}