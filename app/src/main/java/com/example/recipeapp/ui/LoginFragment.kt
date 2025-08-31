package com.example.recipeapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.network.AuthRemoteDataSourceImpl
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.utils.SnackbarUtils
import com.example.recipeapp.viewmodel.AuthUiState
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: Button
    private lateinit var textRegister: TextView
    private lateinit var progressLogin: ProgressBar
    private lateinit var guestBtn: TextView
    private lateinit var guestRegisterLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        emailEt = view.findViewById(R.id.edit_textEmail)
        passwordEt = view.findViewById(R.id.edit_textPassword)
        loginBtn = view.findViewById(R.id.btnLogin)
        textRegister = view.findViewById(R.id.tvRegister)
        progressLogin = view.findViewById(R.id.progressLogin)
        guestBtn = view.findViewById(R.id.btn_guest)
        guestRegisterLayout = textRegister.parent as View

        loginBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    SnackbarUtils.showSnackbar(view, "Please enter email & password", false)
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    SnackbarUtils.showSnackbar(view, "Invalid email format", false)
                    return@setOnClickListener
                }
                else -> {
                    showLoading(true)
                    viewModel.login(email, password)
                }
            }
        }

        guestBtn.setOnClickListener {
            showLoading(true)
            auth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        SnackbarUtils.showSnackbar(view, "Welcome Guest!", true)
                        startActivity(Intent(requireContext(), RecipeActivity::class.java))
                        requireActivity().finish()
                    } else {
                        SnackbarUtils.showSnackbar(view, "Guest login failed!", false)
                    }
                }
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthUiState.Loading -> showLoading(true)
                is AuthUiState.Success -> {
                    showLoading(false)
                    viewModel.resetState()
                    startActivity(Intent(requireContext(), RecipeActivity::class.java))
                    requireActivity().finish()
                }
                is AuthUiState.Error -> {
                    showLoading(false)
                    SnackbarUtils.showSnackbar(view, state.message, false)
                    viewModel.resetState()
                }
                AuthUiState.Idle -> Unit
            }
        }

        textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressLogin.visibility = View.VISIBLE
            loginBtn.visibility = View.GONE
            guestRegisterLayout.visibility = View.GONE
        } else {
            progressLogin.visibility = View.GONE
            loginBtn.visibility = View.VISIBLE
            guestRegisterLayout.visibility = View.VISIBLE
        }
    }
}

