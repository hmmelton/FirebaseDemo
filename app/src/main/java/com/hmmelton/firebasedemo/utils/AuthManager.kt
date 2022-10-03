package com.hmmelton.firebasedemo.utils

import androidx.compose.runtime.MutableState
import com.hmmelton.firebasedemo.data.model.Response

/**
 * Interface for authentication managers
 */
interface AuthManager {
    /**
     * This function returns [MutableState] rather than a plain boolean, because the result of this
     * function call may need to be observed by Compose to trigger a layout recomposition in cases
     * where the UI relies on the user's authentication state.
     *
     * Calls to the [signOut] and sign in/register functions should set a new value for the state
     * returned here.
     */
    fun getAuthenticationState(): MutableState<Boolean>

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