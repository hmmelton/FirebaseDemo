package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.util.PatternsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.data.model.Error
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

    // Used for screen to track UI state
    var uiState by mutableStateOf(AuthUiState())
        private set

    // Used for screen to track form input UI state
    var formUiState by mutableStateOf(AuthFormUiState())
        private set

    // Coroutine job prevents concurrent sign in/registration attempts
    private var authenticationJob: Job? = null

    /**
     * This function processes the sign in button click.
     */
    fun onSignInClick() {
        // TODO(best practices): re-evaluate if this should cancel or keep existing job
        if (authenticationJob != null) return

        authenticationJob = viewModelScope.launch(dispatcher) {
            // Update form UI state with email/password validation
            formUiState = formUiState.copy(
                invalidEmail = !formUiState.isValidEmail(),
                invalidPassword = false
            )

            // Only process click if email and password are valid
            if (!formUiState.invalidEmail) {
                uiState = uiState.copy(isLoading = true)
                val response = authManager.signInWithEmail(formUiState.email, formUiState.password)

                // Update UI state with result of sign in attempt
                uiState = AuthUiState(
                    isLoading = false,
                    errorMessage = (response as? Error)?.messageId,
                    isUserLoggedIn = response is Success
                )
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
            // Update form UI state with email/password validation
            formUiState = formUiState.copy(
                invalidEmail = !formUiState.isValidEmail(),
                invalidPassword = !formUiState.isValidPassword()
            )

            // Only process click if email and password are valid
            if (!formUiState.invalidEmail && !formUiState.invalidPassword) {
                uiState = uiState.copy(isLoading = true)
                val response = authManager.registerWithEmail(
                    formUiState.email, formUiState.password
                )

                // Update UI state with result of registration attempt
                uiState = AuthUiState(
                    isLoading = false,
                    errorMessage = (response as? Error)?.messageId,
                    isUserLoggedIn = response is Success
                )
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
        uiState = uiState.copy(errorMessage = null)
    }

    /**
     * Update email field in auth form
     */
    fun setEmail(email: String) {
        formUiState = formUiState.copy(email = email)
    }

    /**
     * Update password field in auth form.
     */
    fun setPassword(password: String) {
        formUiState = formUiState.copy(password = password)
    }
}

/**
 * Class for tracking UI state of auth screen
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null,
    val isUserLoggedIn: Boolean = false
)

/**
 * Class for tracking UI state of auth form
 */
data class AuthFormUiState(
    val email: String = "",
    val password: String = "",
    val invalidEmail: Boolean = false,
    val invalidPassword: Boolean = false
)

/**
 * This function checks if the given email is formatted correctly.
 */
fun AuthFormUiState.isValidEmail(): Boolean {
    return email.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * This function checks if the given password matches requirements.
 */
fun AuthFormUiState.isValidPassword(): Boolean {
    return password.length >= AuthManager.PASSWORD_MIN_LENGTH
}
