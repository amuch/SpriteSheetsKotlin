package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class DialogConfirmExit(activity: Activity): Dialog(activity)  {
    private var activity: Activity
    private lateinit var imageButtonExitConfirm: ImageButton
    private lateinit var imageButtonExitDeny: ImageButton

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
        imageButtonExitConfirm = findViewById(R.id.image_button_exit_confirm)
        imageButtonExitConfirm.setOnClickListener {
            DrawingActivity.closeApplication()
            this.cancel()
        }

        imageButtonExitDeny = findViewById(R.id.image_button_exit_deny)
        imageButtonExitDeny.setOnClickListener {
            this.cancel()
        }
    }
}