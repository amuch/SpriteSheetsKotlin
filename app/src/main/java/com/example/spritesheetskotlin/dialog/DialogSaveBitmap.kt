package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.DrawingViewModel
import com.example.spritesheetskotlin.R
import java.io.IOException
import java.util.*

class DialogSaveBitmap(activity: DrawingActivity, drawingViewModel: DrawingViewModel): Dialog(activity)  {
    private var activity: DrawingActivity
    private var drawingViewModel: DrawingViewModel
    lateinit var imageViewSave: ImageView
    private lateinit var imageButtonSave: ImageButton
    private lateinit var imageButtonClose: ImageButton

    init {
        setCancelable(false)
        activity.also { this.activity = it }
        drawingViewModel.also{ this.drawingViewModel = it}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_save_bitmap)

        bindUI()
    }

    override fun show() {
        super.show()
        updateBitmap()
    }

    fun updateBitmap() {
        imageViewSave.setImageBitmap(drawingViewModel.bitmapMain.value!!)
    }

    private fun generateRandomString(range : Random, characters : String, length: Int) : String {
        val text = CharArray(length)
        for(i in 1 .. length) {
            text[i - 1] = characters[range.nextInt(characters.length)]
        }
        return String(text)
    }

    private fun writeBitmap() {
        val random = Random()
        val fileName = generateRandomString(random, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 6)
        val appLabel = context.getString(R.string.app_label)
        val saveLocation = "${Environment.DIRECTORY_DCIM}/$appLabel"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, saveLocation)
            }
        }

        val contentResolver = context.contentResolver
        var uri: Uri? = null

        try {
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IOException("Failed to save image.")

            contentResolver.openOutputStream(uri)?.use {
                if(!drawingViewModel.bitmapMain.value!!.compress(Bitmap.CompressFormat.PNG, 60, it)) {
                    throw IOException("Failed to compress bitmap.")
                }
            } ?:
            throw IOException("Failed to open output stream.")
        }
        catch(exception : IOException) {
            uri?.let { orphanUri ->
                contentResolver.delete(orphanUri, null, null)
            }
            exception.printStackTrace()
        }

        Toast.makeText(activity.applicationContext, "Saved $fileName in $saveLocation", Toast.LENGTH_LONG).show()
    }

    private fun bindUI() {
        imageViewSave = findViewById(R.id.imageViewDialogBitmapSave)
        imageViewSave.setImageBitmap(this.drawingViewModel.bitmapMain.value!!)

        imageButtonSave = findViewById<ImageButton>(R.id.imageButtonDialogBitmapSaveConfirm)
        imageButtonSave.setOnClickListener {
            writeBitmap()
            this.cancel()
        }

        imageButtonClose = findViewById<ImageButton>(R.id.imageButtonDialogBitmapSaveClose)
        imageButtonClose.setOnClickListener {
            this.cancel()
        }

    }
}