package com.example.recipeapp

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
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.utils.SnackbarUtils
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val emailEt = view.findViewById<EditText>(R.id.edit_textEmail)
        val passwordEt = view.findViewById<EditText>(R.id.edit_textPassword)
        val loginBtn = view.findViewById<Button>(R.id.btnLogin)
        val textRegister = view.findViewById<TextView>(R.id.tvRegister)
        val progressLogin = view.findViewById<ProgressBar>(R.id.progressLogin)


        loginBtn.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passwordEt.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginBtn.visibility = View.GONE
                progressLogin.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (isAdded) {
                            loginBtn.visibility = View.VISIBLE
                            progressLogin.visibility = View.GONE

                            if (task.isSuccessful) {
                                val intent = Intent(requireContext(), RecipeActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                SnackbarUtils.showSnackbar(
                                    view,
                                    "Login Failed: ${task.exception?.message}",
                                    false
                                )
                            }
                        }
                    }
            } else {
                if (isAdded) {
                    SnackbarUtils.showSnackbar(view, "Please enter email & password", false)
                }
            }
        }

        textRegister.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }
}
