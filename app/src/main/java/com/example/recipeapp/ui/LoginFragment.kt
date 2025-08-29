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

        val emailEt = view.findViewById<EditText>(R.id.edit_textEmail)
        val passwordEt = view.findViewById<EditText>(R.id.edit_textPassword)
        val loginBtn = view.findViewById<Button>(R.id.btnLogin)
        val textRegister = view.findViewById<TextView>(R.id.tvRegister)
        val progressLogin = view.findViewById<ProgressBar>(R.id.progressLogin)
        val guestBtn = view.findViewById<TextView>(R.id.btn_guest)

        // ========== Login with Email & Password ==========
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
                    viewModel.login(email, password)
                }
            }
        }

        // ========== Login as Guest ==========
        guestBtn.setOnClickListener {
            progressLogin.visibility = View.VISIBLE
            loginBtn.visibility = View.GONE

            auth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    progressLogin.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE

                    if (task.isSuccessful) {
                        SnackbarUtils.showSnackbar(view, "Welcome Guest!", true)
                        startActivity(Intent(requireContext(), RecipeActivity::class.java))
                        requireActivity().finish()
                    } else {
                        SnackbarUtils.showSnackbar(view, "Guest login failed!", false)
                    }
                }
        }

        // ========== Observe ViewModel ==========
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    loginBtn.visibility = View.GONE
                    progressLogin.visibility = View.VISIBLE
                }
                is AuthUiState.Success -> {
                    loginBtn.visibility = View.VISIBLE
                    progressLogin.visibility = View.GONE
                    viewModel.resetState()
                    startActivity(Intent(requireContext(), RecipeActivity::class.java))
                    requireActivity().finish()
                }
                is AuthUiState.Error -> {
                    loginBtn.visibility = View.VISIBLE
                    progressLogin.visibility = View.GONE
                    SnackbarUtils.showSnackbar(view, state.message, false)
                    viewModel.resetState()
                }
                AuthUiState.Idle -> Unit
            }
        }

        // ========== Go to Register ==========
        textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}
