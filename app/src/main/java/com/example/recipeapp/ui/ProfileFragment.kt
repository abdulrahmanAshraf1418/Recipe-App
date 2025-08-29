package com.example.recipeapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.R
import com.example.recipeapp.network.AuthRemoteDataSourceImpl
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.AuthViewModelFactory

class ProfileFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tvUserName: TextView
    private lateinit var btnFavorites: Button
    private lateinit var btnLogout: Button
    private lateinit var profileImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvUserName = view.findViewById(R.id.tvUserName)
        btnFavorites = view.findViewById(R.id.btnFavorites)
        btnLogout = view.findViewById(R.id.btnLogout)
        profileImage = view.findViewById(R.id.profileImage)

        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        val currentUser = repo.getCurrentUser()
        tvUserName.text = currentUser?.displayName ?: currentUser?.email ?: "Guest"

        btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.favoritesFragment)
        }

        btnLogout.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        requireContext().showConfirmDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            positiveText = "Yes",
            negativeText = "Cancel",
            onConfirm = {
                viewModel.logout()

                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        )
    }

}
