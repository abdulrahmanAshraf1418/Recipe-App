package com.example.recipeapp.viewmodel

import android.app.AlertDialog
import com.example.recipeapp.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.recipeapp.repository.AuthRepository
import com.example.recipeapp.utils.showConfirmDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val authState: LiveData<AuthUiState> get() = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val user = repository.login(email, password)
                if (user != null) {
                    _authState.value = AuthUiState.Success(user)
                } else {
                    _authState.value = AuthUiState.Error("Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val user = repository.register(name, email, password)
                if (user != null) {
                    _authState.value = AuthUiState.Success(user)
                } else {
                    _authState.value = AuthUiState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val user = repository.signInAsGuest()
                if (user != null) {
                    _authState.value = AuthUiState.Success(user)
                } else {
                    _authState.value = AuthUiState.Error("Guest login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error(e.message ?: "Guest login failed")
            }
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthUiState.Idle
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }

    fun checkUserLoggedIn(): Boolean {
        return if (repository.isLoggedIn()) {
            _authState.value = AuthUiState.Success(repository.getCurrentUser())
            true
        } else {
            _authState.value = AuthUiState.Idle
            false
        }
    }

    fun getCurrentUser() = repository.getCurrentUser()

}
