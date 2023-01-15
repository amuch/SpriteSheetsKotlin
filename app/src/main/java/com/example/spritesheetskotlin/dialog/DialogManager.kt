package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import com.example.spritesheetskotlin.palette.Palette

class DialogManager(activity: Activity, bitmap: Bitmap, palette: Palette) {
    private val dialogManagePalette : DialogManagePalette
    private val dialogSaveBitmap : DialogSaveBitmap
    private val dialogConfirmExit : DialogConfirmExit
    private var bitmapSave = bitmap

    init {
        dialogManagePalette = DialogManagePalette(activity, palette)
        dialogSaveBitmap = DialogSaveBitmap(activity, bitmapSave)
        dialogConfirmExit = DialogConfirmExit(activity)
    }

    fun closeDialogs() {
        if(dialogManagePalette.isShowing) {
            dialogManagePalette.cancel()
        }

        if(dialogSaveBitmap.isShowing) {
            dialogSaveBitmap.cancel()
        }

        if(dialogConfirmExit.isShowing) {
            dialogConfirmExit.cancel()
        }
    }

    fun showDialog(dialogViewModel: DialogViewModel) {
        val widthDialog: Int = Resources.getSystem().displayMetrics.widthPixels * 9 / 10
        val heightDialog: Int = Resources.getSystem().displayMetrics.heightPixels * 9 / 10

        when(dialogViewModel.dialogVisible.value) {
            DialogVisibleEnum.DIALOG_MANAGE_PALETTE -> {
                dialogManagePalette.show()
                dialogManagePalette.window?.setLayout(widthDialog, heightDialog)
                dialogManagePalette.setOnCancelListener{
                    dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_NONE
                }
            }
            DialogVisibleEnum.DIALOG_SAVE_BITMAP -> {
                dialogSaveBitmap.show()
                dialogSaveBitmap.window?.setLayout(widthDialog, heightDialog)
                dialogSaveBitmap.setOnCancelListener {
                    dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_NONE
                }
            }
            DialogVisibleEnum.DIALOG_CONFIRM_EXIT -> {
                dialogConfirmExit.show()
                val sideDialog = Integer.min(widthDialog, heightDialog)
                dialogConfirmExit.window?.setLayout(sideDialog, sideDialog)
                dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_NONE
            }
            DialogVisibleEnum.DIALOG_NONE -> {
                return
            }
        }
    }

}