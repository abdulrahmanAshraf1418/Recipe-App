package com.example.recipeapp.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.core.graphics.toColorInt

object SnackbarUtils {

    fun showSnackbar(view: View, message: String, isSuccess: Boolean) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snackbar.setTextColor(Color.WHITE)

        if (isSuccess) {
            snackbar.setBackgroundTint("#4CAF50".toColorInt())
        } else {
            snackbar.setBackgroundTint("#F44336".toColorInt())
        }

        snackbar.show()
    }
}
