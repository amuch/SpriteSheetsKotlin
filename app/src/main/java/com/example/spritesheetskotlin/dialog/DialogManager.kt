package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.DrawingViewModel
import com.example.spritesheetskotlin.bitmap.BitmapManager
import com.example.spritesheetskotlin.palette.Palette

class DialogManager(activity : DrawingActivity, drawingViewModel: DrawingViewModel, palette: Palette, bitmapManager: BitmapManager) {
    private val drawingActivity: DrawingActivity
    private val dialogManagePalette : DialogManagePalette
    private val dialogSaveBitmap : DialogSaveBitmap
    private val dialogConfirmExit : DialogConfirmExit
    private val dialogConfirmClear: DialogConfirmClear
    private val dialogManageFrame: DialogManageFrame
    private var bitmapManager: BitmapManager

    init {
        activity.also { this.drawingActivity = it }
        bitmapManager.also { this.bitmapManager = it }
        dialogManagePalette = DialogManagePalette(activity as DrawingActivity, palette)
        dialogSaveBitmap = DialogSaveBitmap(activity as DrawingActivity, drawingViewModel)
        dialogConfirmExit = DialogConfirmExit(activity)
        dialogConfirmClear = DialogConfirmClear(activity as DrawingActivity)
        dialogManageFrame = DialogManageFrame(activity as DrawingActivity, bitmapManager, drawingViewModel)
    }

//    fun updateBitmapMain(bitmap: Bitmap) {
//        if(dialogSaveBitmap.isShowing) {
//            dialogSaveBitmap.updateBitmap(bitmap)
//        }
//        if(dialogManageFrame.isShowing) {
//            dialogManageFrame.updateBitmap(bitmap)
//        }
//    }

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

        if(dialogManageFrame.isShowing) {
            dialogManageFrame.cancel()
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
            DialogVisibleEnum.DIALOG_MANAGE_FRAME -> {
                dialogManageFrame.show()
                dialogManageFrame.window?.setLayout(widthDialog, heightDialog)
                dialogManageFrame.setOnCancelListener {
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