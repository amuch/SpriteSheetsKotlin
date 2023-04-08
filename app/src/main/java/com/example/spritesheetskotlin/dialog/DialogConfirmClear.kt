package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class DialogConfirmClear(activity: DrawingActivity): Dialog(activity) {
    private var activity: DrawingActivity
    private lateinit var imageButtonAccept: ImageButton
    private lateinit var imageButtonCancel: ImageButton
    private lateinit var imageViewCurrent: ImageView

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

    override fun show() {
        super.show()
        imageViewCurrent = findViewById(R.id.dialogConfirmClearImageViewCurrent)
        imageViewCurrent.setImageBitmap(activity.bitmapMain())
    }

    private fun bindUI() {
        imageViewCurrent = findViewById(R.id.dialogConfirmClearImageViewCurrent)
        imageViewCurrent.setImageBitmap(activity.bitmapMain())

        imageButtonAccept = findViewById(R.id.imageButtonConfirmClearAccept)
        imageButtonAccept.setOnClickListener {
            activity.clearBitmap()
            this.cancel()
        }

        imageButtonCancel = findViewById(R.id.imageButtonConfirmClearClose)
        imageButtonCancel.setOnClickListener {
            this.cancel()
        }
    }
}