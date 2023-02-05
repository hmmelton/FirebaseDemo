package com.hmmelton.firebasedemo.ui.screens.auth

import androidx.annotation.StringRes
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.data.auth.AuthManager
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Class for tracking UI state of auth screen.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null,
    val isUserLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val invalidEmail: Boolean = false,
    val invalidPassword: Boolean = false
)

/**
 * [ViewModel] used for authentication screen
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    // Used for screen to track UI state
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            AuthUiState()
        )

    // Coroutine job prevents concurrent sign in/registration attempts
    private var authenticationJob: Job? = null

    /**
     * This function processes the sign in button click.
     */
    fun onSignInClick() {
        // TODO(best practices): re-evaluate if this should cancel or keep existing job
        if (authenticationJob != null) return

        authenticationJob = viewModelScope.launch(dispatcher) {
            val state = _uiState.value
            val isValidEmail = isValidEmail(state.email)

            // Update form UI state with email/password validation
            _uiState.update {
                it.copy(
                    invalidEmail = !isValidEmail,
                    invalidPassword = false
                )
            }

            // Only process click if email and password are valid
            if (isValidEmail) {
                _uiState.update { it.copy(isLoading = true) }
                val response =
                    authManager.signInWithEmail(state.email, state.password)

                // Update UI state with result of sign in attempt
                _uiState.update {
                    AuthUiState(
                        isLoading = false,
                        errorMessage = (response as? Error)?.messageId,
                        isUserLoggedIn = response is Success
                    )
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
            val state = _uiState.value
            val isValidEmail = isValidEmail(state.email)
            val isValidPassword = isValidPassword(state.password)

            // Update form UI state with email/password validation
            _uiState.update {
                it.copy(
                    invalidEmail = !isValidEmail,
                    invalidPassword = !isValidPassword
                )
            }

            // Only process click if email and password are valid
            if (isValidEmail && isValidPassword) {
                _uiState.update { it.copy(isLoading = true) }
                val response = authManager.registerWithEmail(state.email, state.password)

                // Update UI state with result of registration attempt
                _uiState.update {
                    AuthUiState(
                        isLoading = false,
                        errorMessage = (response as? Error)?.messageId,
                        isUserLoggedIn = response is Success
                    )
                }
            }

            authenticationJob = null
        }
    }

    /**
     * This function should be called after the user dismisses the error message, or it disappaers
     * on its own.
     */
    fun errorMessageShown() {
        // Remove error message from UI state to avoid accidental re-triggering of one-off event
    }

    /**
     * Update email field in auth form.
     */
    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    /**
     * Update password field in auth form.
     */
    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
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
        return password.length >= AuthManager.PASSWORD_MIN_LENGTH
    }
}
