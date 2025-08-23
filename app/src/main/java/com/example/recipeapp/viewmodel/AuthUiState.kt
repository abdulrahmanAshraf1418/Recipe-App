package com.example.recipeapp.viewmodel

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: com.google.firebase.auth.FirebaseUser?) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}