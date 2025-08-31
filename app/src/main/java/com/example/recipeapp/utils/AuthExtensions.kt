package com.example.recipeapp.utils

import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModelProvider
import com.example.recipeapp.network.AuthRemoteDataSourceImpl
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.ui.AuthActivity
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.AuthViewModelFactory

fun Fragment.checkGuestAction(onSuccess: () -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser?.isAnonymous == true) {
        requireContext().showConfirmDialog(
            title = "Authentication Required",
            message = "You need to register or login to use this feature.",
            positiveText = "Login",
            onConfirm = {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                val repo = AuthRepository(AuthRemoteDataSourceImpl())
                val viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]
                viewModel.logout()
                startActivity(intent)
                requireActivity().finish()
            })
            } else {
        onSuccess()
    }
}
