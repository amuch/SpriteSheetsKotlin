package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.spritesheetskotlin.PREFERENCE_DIMENSION
import com.example.spritesheetskotlin.PREFERENCE_RESOLUTION
import com.example.spritesheetskotlin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

const val NAME_DATA_STORE = "Settings"

class DialogSettings(activity: Activity): Dialog(activity), AdapterView.OnItemSelectedListener {
    private var activity: Activity
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var spinnerDimensions: Spinner
    private lateinit var spinnerResolution: Spinner

    init {
        setCancelable(false)
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_settings)

        dataStore = activity.createDataStore(name = NAME_DATA_STORE)

        bindUI()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selected = parent!!.selectedItem.toString()
        if(R.id.spinner_image_dimension == parent!!.id) {
            coroutineScope.launch {
                saveToDataStore(PREFERENCE_DIMENSION, selected)
            }
        }

        if(R.id.spinner_image_resolution == parent!!.id) {
            coroutineScope.launch {
                saveToDataStore(PREFERENCE_RESOLUTION, selected)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        println("No Selection")
    }

    private suspend fun saveToDataStore(key: String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings->
            settings[dataStoreKey] = value
        }
    }

    suspend fun readFromDataStore(key: String): String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }


    private fun bindUI() {
        spinnerDimensions = findViewById(R.id.spinner_image_dimension)
        setSpinnerArrayAdapter(spinnerDimensions, R.array.image_dimensions)

        spinnerResolution = findViewById(R.id.spinner_image_resolution)
        setSpinnerArrayAdapter(spinnerResolution, R.array.image_resolutions)

        val buttonAccept: Button = findViewById(R.id.button_accept_settings)
        buttonAccept.setOnClickListener {
            this.cancel()
        }
    }

    private fun setSpinnerArrayAdapter(spinner: Spinner, array: Int) {
        ArrayAdapter.createFromResource(
            activity.baseContext,
            array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }
}