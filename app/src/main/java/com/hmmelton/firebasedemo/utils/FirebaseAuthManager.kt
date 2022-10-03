package com.hmmelton.firebasedemo.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Response
import com.hmmelton.firebasedemo.data.model.Success
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val ERROR_MESSAGE = "Error authenticating"
private const val TAG = "UserStateViewModel"

/**
 * [ViewModel] used to track/manage user authentication state.
 */
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth
) : AuthManager {
    private var isAuthenticated = mutableStateOf(auth.currentUser != null)

    override fun getAuthenticationState() = isAuthenticated

    override suspend fun signInWithEmail(email: String, password: String): Response {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            isAuthenticated.value = true
            Success
        } catch (e: Exception) {
            Log.e(TAG, "error signing in", e)
            Error(e.message ?: ERROR_MESSAGE)
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Response {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            isAuthenticated.value = true
            Success
        } catch (e: Exception) {
            Log.e(TAG, "error registering", e)
            Error(e.message ?: ERROR_MESSAGE)
        }
    }

    override fun signOut() {
        auth.signOut()
        auth.addAuthStateListener {  }
        isAuthenticated.value = false
    }
}
