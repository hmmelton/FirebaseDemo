package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Response
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] used for authentication screen
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    // User inputs
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    // Flag if user entered invalid values
    var invalidEmail by mutableStateOf(false)
        private set
    var invalidPassword by mutableStateOf(false)
        private set

    // Used for screen to track UI state
    var uiState = mutableStateOf<AuthUiState>(AuthUiState.AuthInit)
        private set

    // Coroutine job prevents concurrent sign in/registration attempts
    private var authenticationJob: Job? = null

    /**
     * This function processes the sign in button click.
     */
    fun onSignInClick() {
        if (authenticationJob != null) return

        authenticationJob = viewModelScope.launch(dispatcher) {
            invalidEmail = !isValidEmail(email)

            // Only process click if email and password are valid
            if (!invalidEmail) {
                uiState.value = AuthUiState.AuthLoading
                val response = authManager.signInWithEmail(email, password)

                // Update UI state with result of sign in attempt
                uiState.value = if (response is Error) {
                    AuthUiState.AuthFailure(response)
                } else {
                    AuthUiState.AuthSuccess
                }
            }

            authenticationJob = null
        }
    }

    /**
     * This function processes the registration button click.
     */
    fun onRegistrationClick() {
        if (authenticationJob != null) return

        authenticationJob = viewModelScope.launch(dispatcher) {
            invalidEmail = !isValidEmail(email)
            invalidPassword = !isValidPassword(password)

            // Only process click if email and password are valid
            if (!invalidEmail && !invalidPassword) {
                uiState.value = AuthUiState.AuthLoading
                val response = authManager.registerWithEmail(email, password)

                // Update UI state with result of registration attempt
                uiState.value = if (response is Error) {
                    AuthUiState.AuthFailure(response)
                } else {
                    AuthUiState.AuthSuccess
                }
            }

            authenticationJob = null
        }
    }

    /**
     * This function checks if the given email is formatted correctly.
     */
    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * This function checks if the given password matches requirements.
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length > AuthManager.PASSWORD_MIN_LENGTH
    }
}

/**
 * Class for tracking UI state of auth screen
 */
sealed class AuthUiState(val response: Response?) {
    object AuthInit: AuthUiState(null)
    object AuthLoading: AuthUiState(null)
    object AuthSuccess: AuthUiState(Success)
    class AuthFailure(error: Error): AuthUiState(error)

    fun isLoading() = this is AuthInit
}
