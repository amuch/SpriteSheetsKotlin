package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class DialogManageFrame(activity: DrawingActivity): Dialog(activity)  {
    private var activity: DrawingActivity

    init {
        setCancelable(false)
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_manage_frame)

        bindUI()
    }

    private fun bindUI() {

    }
}