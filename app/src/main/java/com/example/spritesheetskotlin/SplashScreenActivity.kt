package com.example.spritesheetskotlin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.example.spritesheetskotlin.database.Database
import com.example.spritesheetskotlin.dialog.DialogSettings
import com.example.spritesheetskotlin.palette.Palette
import com.example.spritesheetskotlin.roomdb.PaletteRoom
import com.example.spritesheetskotlin.roomdb.PixelArtDatabase
import kotlinx.coroutines.*
import kotlin.system.exitProcess

const val DELAY_PREFERENCES : Long = 500
const val DELAY_SPLASHSCREEN : Long = 2500
const val NAME_PREFERENCES = "Settings"
const val PREFERENCE_DIMENSION = "spriteDimension"
const val PREFERENCE_RESOLUTION = "resolution"
const val PLACEHOLDER_PREFERENCE = -1

class SplashScreenActivity : AppCompatActivity() {
    private val coroutineScopeIO = CoroutineScope(Dispatchers.IO)
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    private lateinit var dialogSettings: DialogSettings

    companion object {
        lateinit var database : Database
        var preferenceDimension = 24
        var preferenceResolution = 4
    }

    init {
        database = Database(this)

        //        val dimension = coroutineScopeIO.async {
//            dataStoreManager.readPreference(PREFERENCE_DIMENSION)
//        }
//        dimension.invokeOnCompletion {
//            if(it == null) {
//                println("Dimension ${dimension.getCompleted()}")
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        dataStoreManager = DataStoreManager(applicationContext)

        dialogSettings = DialogSettings(this, database)

        readPreferences()

        coroutineScopeMain.launch {
//            delay(DELAY_PREFERENCES)

            if (preferenceDimension == PLACEHOLDER_PREFERENCE || preferenceResolution == PLACEHOLDER_PREFERENCE) {
                dialogSettingsShow()
                this.cancel()
            }

            delay(DELAY_SPLASHSCREEN)
            launchDrawingActivity(preferenceDimension, preferenceResolution)
        }

//        supportActionBar?.hide()
//
//        Handler().postDelayed({
//            val intent = Intent(this@SplashScreenActivity, DrawingActivity::class.java)
//            intent.putExtra(PREFERENCE_DIMENSION, 24)
//            intent.putExtra(PREFERENCE_RESOLUTION, 8)
//            startActivity(intent)
//            finish()
//        }, DELAY_SPLASHSCREEN)
    }

    private fun dialogSettingsShow() {
        val widthDialog: Int = Resources.getSystem().displayMetrics.widthPixels * 9 / 10
        val heightDialog: Int = Resources.getSystem().displayMetrics.heightPixels * 9 / 10
        val sideDialog = Integer.min(widthDialog, heightDialog)

        runOnUiThread {
            dialogSettings.show()
            dialogSettings.window?.setLayout(widthDialog, heightDialog)
            dialogSettings.setOnCancelListener {
                launchDrawingActivityDefault()
            }
        }
    }

    fun dialogSettings(view: View) {
        coroutineScopeMain.cancel()
        dialogSettingsShow()
    }

    private fun readPreferences() {
        preferenceDimension = database.readPreference(PREFERENCE_DIMENSION)
        preferenceResolution = database.readPreference(PREFERENCE_RESOLUTION)
    }

    private fun launchDrawingActivityDefault() {
        readPreferences()
        if(PLACEHOLDER_PREFERENCE == preferenceDimension) {
            preferenceDimension = 24
        }
        if(PLACEHOLDER_PREFERENCE == preferenceResolution) {
            preferenceResolution = 4
        }
        launchDrawingActivity(preferenceDimension, preferenceResolution)
    }

    private fun launchDrawingActivity(dimension: Int, resolution: Int) {
        val intent = Intent(this@SplashScreenActivity, DrawingActivity::class.java)
        intent.putExtra(PREFERENCE_DIMENSION, dimension)
        intent.putExtra(PREFERENCE_RESOLUTION, resolution)
        startActivity(intent)
        finish()
    }
}