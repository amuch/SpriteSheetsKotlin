package com.example.spritesheetskotlin

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.example.spritesheetskotlin.dialog.DialogSettings
import kotlinx.coroutines.*

const val DELAY_SPLASHSCREEN : Long = 3000
const val NAME_PREFERENCES = "Settings"
const val PREFERENCE_DIMENSION = "spriteDimension"
const val PREFERENCE_RESOLUTION = "resolution"

class SplashScreenActivity : AppCompatActivity() {
    private val dataStoreManager = DataStoreManager(this)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dialogSettings: DialogSettings

    init {
        val dimension = coroutineScope.async {
            dataStoreManager.readPreference(PREFERENCE_DIMENSION)
        }
        dimension.invokeOnCompletion {
            if(it == null) {
                println("Dimension ${dimension.getCompleted()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        dataStore = createDataStore(name = NAME_PREFERENCES)

        dialogSettings = DialogSettings(this)

        /*
        coroutineScope.launch {
            while(!::dataStore.isInitialized) {
                delay(100)
            }
            checkSettings()
        }*/


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

    fun dialogSettingsShow(view: View) {
        val widthDialog: Int = Resources.getSystem().displayMetrics.widthPixels * 9 / 10
        val heightDialog: Int = Resources.getSystem().displayMetrics.heightPixels * 9 / 10
        val sideDialog = Integer.min(widthDialog, heightDialog)

        dialogSettings.show()
        dialogSettings.window?.setLayout(widthDialog, heightDialog)
        dialogSettings.setOnCancelListener {
            launchDrawingActivity()
        }
    }

    private fun checkSettings() {
        val settings = coroutineScope.launch {
            val dimension = dialogSettings.readFromDataStore(PREFERENCE_DIMENSION)!!.toInt()
            if(null == dimension) {
                dialogSettings.show()
                this.cancel()
            }

            val resolution = dialogSettings.readFromDataStore(PREFERENCE_RESOLUTION)!!.toInt()
            if(null == resolution) {
                dialogSettings.show()
                this.cancel()
            }

            delay(DELAY_SPLASHSCREEN)
            launchDrawingActivity()
        }
    }

    private fun launchDrawingActivity() {
        val intent = Intent(this@SplashScreenActivity, DrawingActivity::class.java)
        coroutineScope.launch {
            val dimension = dialogSettings.readFromDataStore(PREFERENCE_DIMENSION)
            intent.putExtra(PREFERENCE_DIMENSION, dimension!!.toInt())
            val resolution = dialogSettings.readFromDataStore(PREFERENCE_RESOLUTION)
            intent.putExtra(PREFERENCE_RESOLUTION, resolution!!.toInt())
            startActivity(intent)
            finish()
        }
    }

    private fun readDataStore(key: String): String? {
        var value: String? = null
        coroutineScope.launch {
            value = dialogSettings.readFromDataStore(key)
        }
        return value
    }
}