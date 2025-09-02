package com.example.recipeapp.ui

import android.R.attr.height
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.recipeapp.R
import com.example.recipeapp.network.AuthRemoteDataSourceImpl
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.utils.showConfirmDialog
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.AuthViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible

class RecipeActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private var isGuest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipe)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repo = AuthRepository(AuthRemoteDataSourceImpl())
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repo))[AuthViewModel::class.java]

        isGuest = viewModel.getCurrentUser()?.isAnonymous == true

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextAppearance(this, R.style.CustomToolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.profileFragment,
            R.id.favoritesFragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                if (!bottomNav.isVisible) {
                    bottomNav.apply {
                        translationY = height.toFloat()
                        visibility = View.VISIBLE
                        animate()
                            .translationY(0f)
                            .setDuration(500)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .start()
                    }
                }
            } else {
                if (bottomNav.isVisible) {
                    bottomNav.animate()
                        .translationY(height.toFloat())
                        .setDuration(500)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withEndAction {
                            bottomNav.visibility = View.GONE
                        }
                        .start()
                }
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.searchFragment, R.id.profileFragment, R.id.favoritesFragment,R.id.notificationsFragment,R.id.detailsFragment,R.id.scheduledMealsFragment)
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        bottomNav.setupWithNavController(navController)

        if (isGuest) {
            bottomNav.menu.removeItem(R.id.favoritesFragment)
            bottomNav.menu.removeItem(R.id.aboutFragment)
        }

        bottomNav.setOnItemSelectedListener { item ->
            if (!isGuest || (item.itemId !=R.id.favoritesFragment)) {
                NavigationUI.onNavDestinationSelected(item, navController)
                true
            } else {
                false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)

        if (isGuest) {
            menu?.findItem(R.id.action_signout)?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.searchFragment)
                true
            }
            R.id.action_about -> {
                if (!isGuest) {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.aboutFragment)
                }
                true
            }
            R.id.action_signout -> {
                if (!isGuest) {
                    showSignOutDialog()
                }
                true
            }
            R.id.action_notification ->{
                findNavController(R.id.nav_host_fragment).navigate(R.id.notificationsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun showSignOutDialog() {
        showConfirmDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            positiveText = "Yes",
            negativeText = "Cancel",
            onConfirm = {
                viewModel.logout()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        )
    }
}
