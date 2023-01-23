package com.hmmelton.firebasedemo.data.auth

import androidx.compose.runtime.MutableState
import com.hmmelton.firebasedemo.data.model.AuthResponse

/**
 * Interface for authentication managers
 */
interface AuthManager {

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }

    fun isAuthenticated(): Boolean

    fun observeAuthState(): MutableState<Boolean>

    /**
     * Sign in an existing user with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResponse

    /**
     * Register a new user with email and password
     */
    suspend fun registerWithEmail(email: String, password: String): AuthResponse

    /**
     * Sign out current user
     */
    fun signOut()
}