package com.example.recipeapp.repository

import com.example.recipeapp.network.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val remoteDataSource: AuthRemoteDataSource) {

    suspend fun login(email: String, password: String): FirebaseUser? =
        remoteDataSource.login(email, password)

    suspend fun register(name: String, email: String, password: String): FirebaseUser? =
        remoteDataSource.register(name, email, password)

    fun logout() = remoteDataSource.logout()

    fun getCurrentUser(): FirebaseUser? = remoteDataSource.getCurrentUser()

    suspend fun signInAsGuest(): FirebaseUser? =
        remoteDataSource.signInAsGuest()
    fun isLoggedIn(): Boolean = remoteDataSource.isLoggedIn()
}
