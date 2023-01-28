package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.palette.Palette

class DialogManager(activity: Activity, bitmapMain: Bitmap, palette: Palette) {
    private val dialogManagePalette : DialogManagePalette
    private val dialogSaveBitmap : DialogSaveBitmap
    private val dialogConfirmExit : DialogConfirmExit
    private val dialogConfirmClear: DialogConfirmClear
    private var bitmapMainLocal = bitmapMain

    init {
        dialogManagePalette = DialogManagePalette(activity, palette)
        dialogSaveBitmap = DialogSaveBitmap(activity, bitmapMainLocal)
        dialogConfirmExit = DialogConfirmExit(activity)
        dialogConfirmClear = DialogConfirmClear(activity as DrawingActivity)
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

        if(dialogConfirmClear.isShowing) {
            dialogConfirmClear.cancel()
        }
    }

    fun showDialog(dialogViewModel: DialogViewModel) {
        val widthDialog: Int = Resources.getSystem().displayMetrics.widthPixels * 9 / 10
        val heightDialog: Int = Resources.getSystem().displayMetrics.heightPixels * 9 / 10
        val sideDialog = Integer.min(widthDialog, heightDialog)

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
                dialogConfirmExit.window?.setLayout(sideDialog, sideDialog)
                dialogConfirmExit.setOnCancelListener {
                    dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_NONE
                }
            }
            DialogVisibleEnum.DIALOG_CONFIRM_CLEAR -> {
                dialogConfirmClear.show()
                dialogConfirmClear.window?.setLayout(sideDialog, sideDialog)
                dialogConfirmClear.setOnCancelListener {
                    dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_NONE
                }
            }
            DialogVisibleEnum.DIALOG_NONE,
            null -> {
                return
            }
        }
    }

}