package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import com.example.spritesheetskotlin.*
import com.example.spritesheetskotlin.database.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val NAME_DATA_STORE = "Settings"
const val NO_PREFERENCE = "0"

class DialogSettings(activity: Activity, database: Database): Dialog(activity), AdapterView.OnItemSelectedListener {
    private var activity: Activity
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var database: Database
    private lateinit var spinnerDimensions: Spinner
    private lateinit var spinnerResolution: Spinner

    init {
        setCancelable(false)
        activity.also {
            this.activity = it
        }
        database.also {
            this.database = it
        }
    }

    companion object {
        var dimensionPreference : String = NO_PREFERENCE
        lateinit var dimensionCurrent: String
        var resolutionPreference: String = NO_PREFERENCE
        lateinit var resolutionCurrent: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_settings)

        bindUI()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selected = parent!!.selectedItem.toString()

        if (R.id.spinner_image_dimension == parent!!.id) {
            dimensionPreference = selected
        }

        if (R.id.spinner_image_resolution == parent!!.id) {
            resolutionPreference = selected
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        println("Nothing selected.")
    }

    private fun bindUI() {
        spinnerDimensions = findViewById(R.id.spinner_image_dimension)
        setSpinnerArrayAdapter(spinnerDimensions, R.array.image_dimensions)

//        val dimensionArray = activity.resources.getStringArray(R.array.image_dimensions)
//        val indexDimensions = currentIndex(dimensionArray, dimensionCurrent)
//        val twentyFourBlocks = 4
//        spinnerDimensions.setSelection(twentyFourBlocks)

        spinnerResolution = findViewById(R.id.spinner_image_resolution)
        setSpinnerArrayAdapter(spinnerResolution, R.array.image_resolutions)

//        val resolutionArray = activity.resources.getStringArray(R.array.image_resolutions)
//        val indexResolutions = currentIndex(resolutionArray, resolutionCurrent)
//        val fourPixels = 2
//        spinnerResolution.setSelection(fourPixels)

        val imageButtonConfirm = findViewById<ImageButton>(R.id.image_button_settings_confirm)
        imageButtonConfirm.setOnClickListener {
            coroutineScope.launch {
                if(NO_PREFERENCE != dimensionPreference) {
                    SplashScreenActivity.preferenceDimension = dimensionPreference.toInt()
                    database.createPreference(PREFERENCE_DIMENSION, dimensionPreference.toInt())
                }
                if(NO_PREFERENCE != resolutionPreference) {
                    SplashScreenActivity.preferenceResolution = resolutionPreference.toInt()
                    database.createPreference(PREFERENCE_RESOLUTION, resolutionPreference.toInt())
                }
            }
            this.cancel()
        }

        val imageButtonExit = findViewById<ImageButton>(R.id.image_button_settings_exit)
        imageButtonExit.setOnClickListener {
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

    private fun currentIndex(array: Array<String>, value: String) : Int {
        for(i in 0 until array.size) {
            if(array[i] == value) {
                return i
            }
        }
        return 0
    }
}