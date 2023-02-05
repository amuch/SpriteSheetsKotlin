package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.example.spritesheetskotlin.R
import java.io.IOException
import java.util.*

class DialogSaveBitmap(activity: Activity, bitmap: Bitmap): Dialog(activity)  {
    private var activity: Activity
    private var bitmap: Bitmap
    lateinit var imageViewSave: ImageView
    private lateinit var imageButtonSave: ImageButton
    private lateinit var buttonCancel: Button

    init {
        setCancelable(false)
        bitmap.also { this.bitmap = it }
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_save_bitmap)

        bindUI()
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
                if(!bitmap.compress(Bitmap.CompressFormat.PNG, 60, it)) {
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

//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream)
//
//        val random = Random()
//        val randomCharacters = generateRandomString(random, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 6)
//        val fileName = "$randomCharacters.png"
//
//        val fileOutputStream = FileOutputStream("/Android/data/${R.string.app_name}/$fileName")
//        var fileSaveResult = "Saved $fileName to ${fileOutputStream.toString()}"
//        try {
//            fileOutputStream.write(byteArrayOutputStream.toByteArray())
//            fileOutputStream.close()
//        }
//        catch(exception: Exception) {
//            exception.printStackTrace()
//            fileSaveResult = "Unable to save: ${exception.toString()}"
//        }
//        Toast.makeText(activity.applicationContext, fileSaveResult, Toast.LENGTH_LONG).show()
    }

    private fun bindUI() {
        imageViewSave = findViewById(R.id.imageViewDialogBitmapSave)
        imageViewSave.setImageBitmap(this.bitmap)

        imageButtonSave = findViewById<ImageButton>(R.id.buttonDialogBitmapSaveConfirm)
        imageButtonSave.setOnClickListener {
            writeBitmap()
            this.cancel()
        }

        buttonCancel = findViewById<Button>(R.id.buttonDialogBitmapSaveCancel)
        buttonCancel.setOnClickListener {
            this.cancel()
        }

    }
}