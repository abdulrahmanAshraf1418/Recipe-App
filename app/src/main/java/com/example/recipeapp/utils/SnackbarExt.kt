package com.example.recipeapp.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.example.recipeapp.R
import com.google.android.material.snackbar.Snackbar

fun View.showStyledSnackBar(
    message: String,
    actionText: String? = null,
    action: (() -> Unit)? = null
) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)

    snackBar.setBackgroundTint(ContextCompat.getColor(context, android.R.color.white))
    snackBar.setTextColor(ContextCompat.getColor(context, android.R.color.black))

    actionText?.let {
        snackBar.setAction(it) { action?.invoke() }
        snackBar.setActionTextColor(ContextCompat.getColor(context, R.color.primaryColor))
    }

    snackBar.show()
}
