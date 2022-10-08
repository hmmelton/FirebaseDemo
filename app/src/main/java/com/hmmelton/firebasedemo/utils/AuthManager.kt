package com.hmmelton.firebasedemo.utils

import com.hmmelton.firebasedemo.data.model.Response

/**
 * Interface for authentication managers
 */
interface AuthManager {

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }

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