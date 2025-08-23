package com.example.recipeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.repository.AuthRepository
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
}
