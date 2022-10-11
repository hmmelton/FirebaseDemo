package com.hmmelton.firebasedemo.ui.viewmodels

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.data.model.Response
import com.hmmelton.firebasedemo.utils.AuthManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [ViewModel] used for authentication screen
 */
open class AuthViewModel @Inject constructor(
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

    /**
     * This function processes the sign in button click.
     * @param onResult function that takes the authentication response
     */
    fun onSignInClick(onResult: (Response) -> Unit) {
        invalidEmail = !isValidEmail(email)
        invalidPassword = isValidPassword(password)

        // Only process click if email and password are valid
        if (!invalidEmail && !invalidPassword) {
            viewModelScope.launch(dispatcher) {
                val response = authManager.signInWithEmail(email, password)
                onResult(response)
            }
        }
    }

    /**
     * This function processes the registration button click.
     * @param onResult function that takes the authentication response
     */
    fun onRegistrationClick(onResult: (Response) -> Unit) {
        invalidEmail = !isValidEmail(email)
        invalidPassword = isValidPassword(password)

        // Only process click if email and password are valid
        if (!invalidEmail && !invalidPassword) {
            viewModelScope.launch(dispatcher) {
                val response = authManager.registerWithEmail(email, password)
                onResult(response)
            }
        }
    }

    /**
     * This function checks if the given email is formatted correctly.
     */
    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * This function checks if the given password matches requirements.
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length > AuthManager.PASSWORD_MIN_LENGTH
    }
}