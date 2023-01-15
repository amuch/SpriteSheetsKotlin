package com.example.spritesheetskotlin.menu

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R

class MenuActivity : AppCompatActivity() {
    lateinit var menuViewModel: MenuViewModel
    private var dialogVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        menuViewModel.nameProject.observe(this) {}
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putBoolean(R.string.dialog_visible_menu_activity.toString(), dialogVisible)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        if(bundle.getBoolean(R.string.dialog_visible_menu_activity.toString())) {
            dialogVisible = true
            projectNew()
        }
    }

    fun projectDialog(view: View) {
        dialogVisible = true
        projectNew()
    }

    private fun projectNew() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_project_new)

        val editText = dialog.findViewById(R.id.editTextDialogProjectNew) as EditText
        editText.setText(menuViewModel.nameProject.value)
        val acceptProject = dialog.findViewById(R.id.buttonDialogProjectNewAccept) as Button
        val cancelProject = dialog.findViewById(R.id.buttonDialogProjectNewCancel) as Button

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                menuViewModel.nameProject.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        acceptProject.setOnClickListener {
            menuViewModel.nameProject.value?.let { it1 -> drawingActivityLaunch(this, it1) }
        }

        cancelProject.setOnClickListener {
            dialogVisible = false
            dialog.cancel()
        }

        dialog.show()
        val widthDialog: Int = resources.displayMetrics.widthPixels * 9 / 10
        val heightDialog: Int = resources.displayMetrics.heightPixels * 9 / 10
        dialog.window?.setLayout(widthDialog, heightDialog)
    }

    private fun drawingActivityLaunch(context: Context, name: String) {
        val intent = Intent(context, DrawingActivity::class.java)
        intent.putExtra("nameProject", name)
        startActivity(intent)
    }
}