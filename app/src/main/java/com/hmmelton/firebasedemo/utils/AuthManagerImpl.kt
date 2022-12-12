package com.hmmelton.firebasedemo.utils

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.AuthType
import com.hmmelton.firebasedemo.analytics.events.FetchUserFailureEvent
import com.hmmelton.firebasedemo.analytics.events.RegistrationFailureEvent
import com.hmmelton.firebasedemo.analytics.events.SignInFailureEvent
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.AuthResponse
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.data.model.User
import com.hmmelton.firebasedemo.data.repository.Repository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "FirebaseAuthManager"

/**
 * [ViewModel] used to track/manage user authentication state.
 */
class AuthManagerImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: Repository<User>,
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

    override suspend fun signInWithEmail(email: String, password: String): AuthResponse {
        return try {
            // Attempt to sign in and fetch authenticated FirebaseUser object
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Error(R.string.sign_in_error)

            // If user was authenticated, create User object and save to server
            val user = userRepository.get(firebaseUser.uid)

            if (user != null) {
                // Set authenticated user's ID to analytics for tracking
                analytics.setUserId(firebaseUser.uid)
                Success
            } else {
                // Sign the user out if the User object cannot be read from the remote database
                signOut()
                analytics.logEvent(FetchUserFailureEvent(false))
                Error(R.string.sign_in_error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "error signing in", e)
            analytics.logEvent(SignInFailureEvent(e, AuthType.EMAIL))
            Error(R.string.sign_in_error)
        }
    }

    override suspend fun registerWithEmail(email: String, password: String): AuthResponse {
        return try {
            // Attempt to register user and fetch authenticated FirebaseUser object
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Error(R.string.registration_error)

            // If user was authenticated, create User object and save to server
            val user = userRepository.get(firebaseUser.uid)

            if (user != null) {
                // Set authenticated user's ID to analytics for tracking
                analytics.setUserId(firebaseUser.uid)
                Success
            } else {
                // Sign the user out if the User object cannot be read from the remote database
                signOut()
                analytics.logEvent(FetchUserFailureEvent(true))
                Error(R.string.registration_error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "error registering", e)
            analytics.logEvent(RegistrationFailureEvent(e, AuthType.EMAIL))
            Error(R.string.registration_error)
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
