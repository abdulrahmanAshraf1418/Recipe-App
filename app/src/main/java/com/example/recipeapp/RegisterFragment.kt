package com.example.recipeapp

import android.content.Intent
import android.graphics.Color
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

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

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerBtn.visibility = View.GONE
                progressRegister.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        registerBtn.visibility = View.VISIBLE
                        progressRegister.visibility = View.GONE

                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                                SnackbarUtils.showSnackbar(view, "Register Successful", true)

                                val intent = Intent(requireContext(), RecipeActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } else {
                            SnackbarUtils.showSnackbar(
                                view,
                                "Register Failed: ${task.exception?.message}",
                                false
                            )
                        }
                    }
            } else {
                SnackbarUtils.showSnackbar(view, "Please enter name, email & password", false)
            }
        }

        textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

}
