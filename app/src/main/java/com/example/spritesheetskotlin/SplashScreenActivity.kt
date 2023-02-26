package com.example.spritesheetskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

const val DELAY_SPLASHSCREEN : Long = 3000

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, DrawingActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_SPLASHSCREEN)
    }
}