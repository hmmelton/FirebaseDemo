package com.hmmelton.firebasedemo.utils

import androidx.compose.runtime.MutableState
import com.hmmelton.firebasedemo.data.model.Response

/**
 * Interface for authentication managers
 */
interface AuthManager {

    val isAuthenticated: Boolean

    /**
     * Sign in an existing user with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): Response

    /**
     * Register a new user with email and password
     */
    suspend fun registerWithEmail(email: String, password: String): Response

    /**
     * Sign out current user
     */
    fun signOut()
}