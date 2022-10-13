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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SIGN_IN_ERROR = "Failed to sign in"
private const val REGISTRATION_ERROR = "Failed to register"
private const val TAG = "FirebaseAuthManager"

/**
 * [ViewModel] used to track/manage user authentication state.
 */
class FirebaseAuthManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val dispatcher: CoroutineDispatcher
) : AuthManager {

    private var _isAuthenticated = mutableStateOf(auth.currentUser != null)

    init {
        auth.addAuthStateListener { newAuth ->
            _isAuthenticated.value = newAuth.currentUser != null
        }
    }

    override fun isAuthenticated() = _isAuthenticated.value

    override fun observeAuthState() = _isAuthenticated

    override suspend fun signInWithEmail(email: String, password: String): Response {
        return withContext(dispatcher) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Success
            } catch (e: Exception) {
                Log.e(TAG, "error signing in", e)
                Error(SIGN_IN_ERROR)
            }
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Response {
        return withContext(dispatcher) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                Success
            } catch (e: Exception) {
                Log.e(TAG, "error registering", e)
                Error(REGISTRATION_ERROR)
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
