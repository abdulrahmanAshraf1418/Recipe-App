package com.example.recipeapp.ui

import android.content.Intent
import android.os.Bundle
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

class RegisterFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        val nameEt = view.findViewById<EditText>(R.id.edit_textName)
        val emailEt = view.findViewById<EditText>(R.id.edit_textEmail)
        val passwordEt = view.findViewById<EditText>(R.id.edit_textPassword)
        val registerBtn = view.findViewById<Button>(R.id.btnRegister)
        val textLogin = view.findViewById<TextView>(R.id.tvLogin)
        val progressRegister = view.findViewById<ProgressBar>(R.id.progressRegister)

        registerBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                SnackbarUtils.showSnackbar(view, "Please enter name, email & password", false)
                return@setOnClickListener
            }
                viewModel.register(name, email, password)
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    setRegisterLoading(true, registerBtn, textLogin, progressRegister)
                }
                is AuthUiState.Success -> {
                    setRegisterLoading(false, registerBtn, textLogin, progressRegister)
                    SnackbarUtils.showSnackbar(view, "Register Successful", true)
                    viewModel.resetState()
                    startActivity(Intent(requireContext(), RecipeActivity::class.java))
                    requireActivity().finish()
                }
                is AuthUiState.Error -> {
                    setRegisterLoading(false, registerBtn, textLogin, progressRegister)
                    SnackbarUtils.showSnackbar(view, state.message, false)
                    viewModel.resetState()
                }
                AuthUiState.Idle -> Unit
            }
        }


        textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}

private fun setRegisterLoading(isLoading: Boolean, registerBtn: View, loginText: View, progress: View) {
    if (isLoading) {
        registerBtn.visibility = View.GONE
        loginText.visibility = View.GONE
        progress.visibility = View.VISIBLE
    } else {
        registerBtn.visibility = View.VISIBLE
        loginText.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }
}

