package com.hmmelton.firebasedemo.utils

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.AuthType
import com.hmmelton.firebasedemo.analytics.events.RegistrationFailureEvent
import com.hmmelton.firebasedemo.analytics.events.SignInFailureEvent
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Response
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.data.model.User
import com.hmmelton.firebasedemo.data.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val SIGN_IN_ERROR = "Failed to sign in"
private const val REGISTRATION_ERROR = "Failed to register"
private const val TAG = "FirebaseAuthManager"

/**
 * [ViewModel] used to track/manage user authentication state.
 */
class AuthManagerImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: Repository<User>,
    private val dispatcher: CoroutineDispatcher,
    private val analytics: AnalyticsClient
) : AuthManager {

    private var _isAuthenticated = mutableStateOf(auth.currentUser != null)

    init {
        auth.addAuthStateListener { newAuth ->
            _isAuthenticated.value = newAuth.currentUser != null
        }
    }

    override fun isAuthenticated() = auth.currentUser != null

    override fun observeAuthState() = _isAuthenticated

    override suspend fun signInWithEmail(email: String, password: String): Response {
        return withContext(dispatcher) {
            try {
                // Attempt to sign in and fetch authenticated FirebaseUser object
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user ?: return@withContext Error(REGISTRATION_ERROR)

                // If user was authenticated, create User object and save to server
                val user = userRepository.get(firebaseUser.uid)

                if (user != null) {
                    // Set authenticated user's ID to analytics for tracking
                    analytics.setUserId(firebaseUser.uid)
                    Success
                } else {
                    // Sign the user out if the User object cannot be read from the remote database
                    signOut()
                    Error(REGISTRATION_ERROR)
                }
            } catch (e: Exception) {
                Log.e(TAG, "error signing in", e)
                analytics.logEvent(SignInFailureEvent(e, AuthType.EMAIL))
                Error(SIGN_IN_ERROR)
            }
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): Response {
        return withContext(dispatcher) {
            try {
                // Attempt to register user and fetch authenticated FirebaseUser object
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user ?: return@withContext Error(REGISTRATION_ERROR)

                // If user was authenticated, create User object and save to server
                val user = userRepository.get(firebaseUser.uid)

                if (user != null) {
                    // Set authenticated user's ID to analytics for tracking
                    analytics.setUserId(firebaseUser.uid)
                    Success
                } else {
                    // Sign the user out if the User object cannot be read from the remote database
                    signOut()
                    Error(REGISTRATION_ERROR)
                }
            } catch (e: Exception) {
                Log.e(TAG, "error registering", e)
                analytics.logEvent(RegistrationFailureEvent(e, AuthType.EMAIL))
                Error(REGISTRATION_ERROR)
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
