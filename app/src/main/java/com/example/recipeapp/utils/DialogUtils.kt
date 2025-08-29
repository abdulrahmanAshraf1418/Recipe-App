package com.example.recipeapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.recipeapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showConfirmDialog(
    title: String,
    message: String,
    positiveText: String = "Yes",
    negativeText: String = "Cancel",
    onConfirm: () -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setBackground(ContextCompat.getDrawable(this, R.drawable.bg_white_dialog))
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ ->
            onConfirm()
        }
        .setNegativeButton(negativeText) { d, _ ->
            d.dismiss()
        }
        .show()

    // 🎨 تخصيص الأزرار بعد ما الـ Dialog يظهر
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
        textSize = 18f
        setAllCaps(false)
    }
    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
        textSize = 14f
        setTextColor(ContextCompat.getColor(this@showConfirmDialog, R.color.gray))
        setAllCaps(false)
    }
}
