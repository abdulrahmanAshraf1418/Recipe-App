package com.example.recipeapp.utils

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.core.net.toUri

fun Fragment.checkGuestAction(onSuccess: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser?.isAnonymous == true) {
        requireContext().showConfirmDialog(
            title = "Login Required",
            message = "You need to register or login to use this feature.",
            positiveText = "Login",
            negativeText = "Cancel"
        ) {
            // محتاجه تتعدل
            findNavController().navigate("app://recipeapp/login".toUri())
        }

    } else {
        onSuccess()
    }
}
