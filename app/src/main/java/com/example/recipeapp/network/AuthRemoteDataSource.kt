package com.example.recipeapp.network

import com.google.firebase.auth.FirebaseUser

interface AuthRemoteDataSource {
    fun getCurrentUser(): FirebaseUser?
    suspend fun login(email: String, password: String): FirebaseUser?
    suspend fun register(name: String, email: String, password: String): FirebaseUser?
    fun logout()
    fun isLoggedIn(): Boolean
}
