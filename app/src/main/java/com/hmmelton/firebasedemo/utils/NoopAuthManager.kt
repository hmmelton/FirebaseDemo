package com.hmmelton.firebasedemo.utils

import androidx.compose.runtime.MutableState
import com.hmmelton.firebasedemo.data.model.Response

/**
 * Used for Composition previews
 */
class NoopAuthManager() : AuthManager {
    override fun getAuthenticationState(): MutableState<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmail(email: String, password: String): Response {
        TODO("Not yet implemented")
    }

    override suspend fun registerWithEmail(email: String, password: String): Response {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }
}