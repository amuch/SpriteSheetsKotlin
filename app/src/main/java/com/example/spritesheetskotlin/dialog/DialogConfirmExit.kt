package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class DialogConfirmExit(activity: Activity): Dialog(activity)  {
    private var activity: Activity
    private lateinit var buttonConfirmExitTrue: Button
    private lateinit var buttonConfirmExitFalse: Button

    init {
        setCancelable(false)
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm_exit)

        bindUI()
    }

    private fun bindUI() {
        buttonConfirmExitTrue = findViewById(R.id.button_confirm_exit_true)
        buttonConfirmExitTrue.setOnClickListener {
            DrawingActivity.closeApplication()
            this.cancel()
        }

        buttonConfirmExitFalse = findViewById(R.id.button_confirm_exit_false)
        buttonConfirmExitFalse.setOnClickListener {
            this.cancel()
        }
    }
}