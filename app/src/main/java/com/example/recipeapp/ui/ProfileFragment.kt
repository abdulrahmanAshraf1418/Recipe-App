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
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import com.example.recipeapp.network.AuthRemoteDataSourceImpl
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.AuthViewModelFactory
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class ProfileFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var tvGreeting: TextView
    private lateinit var tvUserName: TextView
    private lateinit var btnFavorites: MaterialButton
    private lateinit var btnLogout: Button
    private lateinit var btnGuestLogin: MaterialButton
    private lateinit var profileImage: ImageView
    private lateinit var userButtonsLayout: View
    private lateinit var guestButtonsLayout: View

    private lateinit var btnScheduled: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvUserName = view.findViewById(R.id.tvUserName)
        btnFavorites = view.findViewById(R.id.btnFavorites)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnGuestLogin = view.findViewById(R.id.guest_buttons)
        profileImage = view.findViewById(R.id.profileImage)
        userButtonsLayout = view.findViewById(R.id.User_buttons)
        guestButtonsLayout = view.findViewById(R.id.guest_buttons)
        btnScheduled = view.findViewById(R.id.scheduled_meal)

        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        setupUI(repo)
        setupClickListeners()
    }

    private fun setupUI(repo: AuthRepository) {
        val currentUser = repo.getCurrentUser()

        if (repo.getCurrentUser()?.isAnonymous == true) {
            showGuestUI()
        } else {
            showUserUI(currentUser?.displayName ?: currentUser?.email ?: "User")
        }
    }

    private fun showUserUI(userName: String) {
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        tvGreeting.text = "$greeting, My Chef"
        tvUserName.text = userName

        userButtonsLayout.visibility = View.VISIBLE
        guestButtonsLayout.visibility = View.GONE

        val currentUser = viewModel.getCurrentUser()
        val photoUrl = currentUser?.photoUrl

        Glide.with(this)
            .load(photoUrl ?: R.drawable.chef_logo1)
            .circleCrop()
            .into(profileImage)

    }


    private fun showGuestUI() {
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        tvGreeting.text = "$greeting, Guest"
        tvUserName.text = "Please login to access all features"

        userButtonsLayout.visibility = View.GONE
        guestButtonsLayout.visibility = View.VISIBLE

        Glide.with(this)
            .load(R.drawable.chef_logo1)
            .circleCrop()
            .into(profileImage)
    }

    private fun setupClickListeners() {
        btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.favoritesFragment)
        }

        btnLogout.setOnClickListener {
            showSignOutDialog()
        }

        btnGuestLogin.setOnClickListener {
            viewModel.logout()
            navigateToAuth()
        }

        btnScheduled.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToScheduledMealsFragment()
            findNavController().navigate(action)
        }
    }

    private fun navigateToAuth() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showSignOutDialog() {
        requireContext().showConfirmDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            positiveText = "Yes",
            negativeText = "Cancel",
            onConfirm = {
                viewModel.logout()
                navigateToAuth()
            }
        )
    }

    override fun onResume() {
        super.onResume()
        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        setupUI(repo)
    }
}