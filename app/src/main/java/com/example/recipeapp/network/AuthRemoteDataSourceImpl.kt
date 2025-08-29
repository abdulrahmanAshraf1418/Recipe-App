package com.example.recipeapp.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class AuthRemoteDataSourceImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRemoteDataSource {

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun login(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> throw Exception("No account found with this email")
                is FirebaseAuthInvalidCredentialsException -> throw Exception("Invalid email or password")
                else -> throw Exception(e.message ?: "Login failed")
            }
        }
    }

    override suspend fun register(name: String, email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )?.await()
            auth.currentUser
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> throw Exception("This email is already in use")
                is FirebaseAuthWeakPasswordException -> throw Exception("Password is too weak, must be at least 6 characters")
                is FirebaseAuthInvalidCredentialsException -> throw Exception("Invalid email format")
                else -> throw Exception(e.message ?: "Registration failed")
            }
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun signInAsGuest(): FirebaseUser? {
        return suspendCoroutine { cont ->
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(task.result?.user)
                    } else {
                        cont.resumeWithException(task.exception ?: Exception("Guest login failed"))
                    }
                }
        }
    }



    override fun isLoggedIn(): Boolean = auth.currentUser != null
}
