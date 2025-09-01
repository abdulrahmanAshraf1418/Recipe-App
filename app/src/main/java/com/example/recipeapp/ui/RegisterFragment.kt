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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegisterFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_register, container, false)

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
        val googleBtn = view.findViewById<Button>(R.id.btnGoogleRegister)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // من Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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

        googleBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthUiState.Loading -> {
                    setRegisterLoading(true, registerBtn, textLogin, progressRegister, googleBtn)
                }
                is AuthUiState.Success -> {
                    setRegisterLoading(false, registerBtn, textLogin, progressRegister, googleBtn)
                    SnackbarUtils.showSnackbar(view, "Register Successful", true)
                    viewModel.resetState()
                    startActivity(Intent(requireContext(), RecipeActivity::class.java))
                    requireActivity().finish()
                }
                is AuthUiState.Error -> {
                    setRegisterLoading(false, registerBtn, textLogin, progressRegister, googleBtn)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                SnackbarUtils.showSnackbar(requireView(), "Google sign in failed: ${e.message}", false)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val name = user?.displayName ?: "Unknown"
                    val email = user?.email ?: "No Email"
                    val photoUrl = user?.photoUrl

                    SnackbarUtils.showSnackbar(requireView(), "Welcome $name", true)

                    // 🔹 هنا تقدر تبعت البيانات للـ ViewModel أو تحفظها في Firestore
                    startActivity(Intent(requireContext(), RecipeActivity::class.java))
                    requireActivity().finish()
                } else {
                    SnackbarUtils.showSnackbar(requireView(), "Authentication Failed.", false)
                }
            }
    }
}

private fun setRegisterLoading(
    isLoading: Boolean,
    registerBtn: View,
    loginText: View,
    progress: View,
    googleBtn: View
) {
    if (isLoading) {
        registerBtn.isEnabled = false
        googleBtn.isEnabled = false
        loginText.visibility = View.GONE
        progress.visibility = View.VISIBLE
    } else {
        registerBtn.isEnabled = true
        googleBtn.isEnabled = true
        loginText.visibility = View.VISIBLE
        progress.visibility = View.GONE
    }
}
