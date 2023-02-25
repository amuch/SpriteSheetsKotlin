package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class DialogConfirmClear(activity: DrawingActivity): Dialog(activity) {
    private var activity: DrawingActivity
    private lateinit var buttonConfirmClearTrue: Button
    private lateinit var buttonConfirmClearFalse: Button

    init {
        setCancelable(false)
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_confirm_clear)

        bindUI()
    }

    private fun bindUI() {
        buttonConfirmClearTrue = findViewById(R.id.button_confirm_clear_true)
        buttonConfirmClearTrue.setOnClickListener {
            activity.clearBitmap()
            this.cancel()
        }

        buttonConfirmClearFalse = findViewById(R.id.button_confirm_clear_false)
        buttonConfirmClearFalse.setOnClickListener {
            this.cancel()
        }
    }
}