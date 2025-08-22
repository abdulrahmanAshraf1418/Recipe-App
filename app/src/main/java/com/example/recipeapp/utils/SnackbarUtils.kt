package com.example.recipeapp.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackbarUtils {

    fun showSnackbar(view: View, message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setTextColor(Color.WHITE)

        if (isSuccess) {
            snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
        } else {
            snackbar.setBackgroundTint(Color.parseColor("#F44336"))
        }

        snackbar.show()
    }
}
