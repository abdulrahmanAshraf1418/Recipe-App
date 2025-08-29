package com.example.recipeapp.utils

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.core.net.toUri

fun Fragment.checkGuestAction(onSuccess: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser?.isAnonymous == true) {
        AlertDialog.Builder(requireContext())
            .setTitle("Login Required")
            .setMessage("You need to register or login to use this feature.")
            .setPositiveButton("Login") { _, _ ->
                findNavController().navigate("app://recipeapp/login".toUri())
            }
            .setNegativeButton("Cancel", null)
            .show()
    } else {
        onSuccess()
    }
}
