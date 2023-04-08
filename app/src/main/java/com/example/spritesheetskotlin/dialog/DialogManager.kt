package com.example.spritesheetskotlin.dialog

import android.content.res.Resources
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.DrawingViewModel
import com.example.spritesheetskotlin.bitmap.BitmapManager
import com.example.spritesheetskotlin.database.Database
import com.example.spritesheetskotlin.palette.PaletteViewModel

class DialogManager(activity : DrawingActivity, drawingViewModel: DrawingViewModel, paletteViewModel: PaletteViewModel, dialogViewModel: DialogViewModel, bitmapManager: BitmapManager, database: Database) {
    private val drawingActivity: DrawingActivity
    private val dialogManagePalette : DialogManagePalette
    private val dialogSaveBitmap : DialogSaveBitmap
    private val dialogViewModel : DialogViewModel
    private val dialogConfirmExit : DialogConfirmExit
    private val dialogConfirmClear: DialogConfirmClear
    private val dialogManageFrame: DialogManageFrame
    private val dialogSettings: DialogSettings
    private var bitmapManager: BitmapManager

    init {
        activity.also { this.drawingActivity = it }
        bitmapManager.also { this.bitmapManager = it }
        dialogViewModel.also { this.dialogViewModel = it }
        dialogManagePalette = DialogManagePalette(activity as DrawingActivity, paletteViewModel)
        dialogSaveBitmap = DialogSaveBitmap(activity as DrawingActivity, drawingViewModel)
        dialogConfirmExit = DialogConfirmExit(activity)
        dialogConfirmClear = DialogConfirmClear(activity as DrawingActivity)
        dialogManageFrame = DialogManageFrame(activity as DrawingActivity, bitmapManager, drawingViewModel)
        dialogSettings = DialogSettings(activity, database)
    }

    fun closeDialogs() {
        if(dialogManagePalette.isShowing) {
            dialogManagePalette.dismiss()
        }

        if(dialogSaveBitmap.isShowing) {
            dialogSaveBitmap.dismiss()
        }

        if(dialogConfirmExit.isShowing) {
            dialogConfirmExit.dismiss()
        }

        if(dialogConfirmClear.isShowing) {
            dialogConfirmClear.dismiss()
        }

        if(dialogManageFrame.isShowing) {
            dialogManageFrame.dismiss()
        }

        if(dialogSettings.isShowing) {
            dialogSettings.dismiss()
        }
    }

    fun showDialog() {
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
                dialogSaveBitmap.window?.setLayout(sideDialog, sideDialog)
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
            DialogVisibleEnum.DIALOG_SETTINGS -> {
                dialogSettings.show()
                dialogSettings.window?.setLayout(sideDialog, sideDialog)
                dialogSettings.setOnCancelListener {
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