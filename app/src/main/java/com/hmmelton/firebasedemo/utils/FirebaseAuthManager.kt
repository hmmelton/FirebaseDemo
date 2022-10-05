package com.hmmelton.firebasedemo.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Response
import com.hmmelton.firebasedemo.data.model.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val ERROR_MESSAGE = "Error authenticating"
private const val TAG = "FirebaseAuthManager"

/**
 * [ViewModel] used to track/manage user authentication state.
 */
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth
) : AuthManager {
    override val isAuthenticated: Boolean
        get() = auth.currentUser != null

    override suspend fun signInWithEmail(email: String, password: String): Response {
        return withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Success
            } catch (e: Exception) {
                Log.e(TAG, "error signing in", e)
                Error(e.message ?: ERROR_MESSAGE)
            }
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Response {
        return withContext(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Success
            } catch (e: Exception) {
                Log.e(TAG, "error registering", e)
                Error(e.message ?: ERROR_MESSAGE)
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
