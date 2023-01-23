package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.core.util.PatternsCompat
import com.hmmelton.firebasedemo.MainDispatcherRule
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.data.auth.AuthManager
import com.hmmelton.firebasedemo.ui.screens.auth.AuthFormUiState
import com.hmmelton.firebasedemo.ui.screens.auth.AuthUiState
import com.hmmelton.firebasedemo.ui.screens.auth.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    companion object {
        private const val VALID_EMAIL = "harrison@test.com"
        private const val VALID_PASSWORD = "12345678"
        private const val INVALID_PASSWORD = "123" // Only rule is must be >= 8 characters
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authManager: AuthManager
    private lateinit var subject: AuthViewModel

    @Before
    fun `set up`() {
        authManager = mockk()
        mockkObject(PatternsCompat.EMAIL_ADDRESS)
        every { PatternsCompat.EMAIL_ADDRESS.matcher(any()).matches() } returns true

        subject = AuthViewModel(authManager, mainDispatcherRule.testDispatcher)
    }

    @Test
    fun `check initial field values are correct`() {
        val uiState = subject.uiState
        val formUiState = subject.formUiState

        validateInitAuthUiState(uiState)
        validateInitAuthFormUiState(formUiState)
    }

    @Test
    fun `onSignInClick with invalid email causes early return`() {
        subject.onSignInClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertTrue(subject.formUiState.invalidEmail)
        coVerify(exactly = 0) { authManager.signInWithEmail(any(), any()) }
    }

    @Test
    fun `onSignInClick updates uiState with error when auth fails`() {
        givenValidCredentials()
        val authError = Error(0)
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers  {
            Assert.assertTrue(subject.uiState.isLoading)
            authError
        }

        subject.onSignInClick()

        validateAuthError(subject.uiState)
        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    @Test
    fun `onSignInClick updates uiState with success when auth succeeds`() {
        givenValidCredentials()
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers {
            Assert.assertTrue(subject.uiState.isLoading)
            Success
        }

        subject.onSignInClick()

        validateAuthSuccess(subject.uiState)
        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    @Test
    fun `onRegistrationClick with invalid email causes early return`() {
        // Set to ensure failure is from bad email
        subject.setPassword(VALID_PASSWORD)

        subject.onRegistrationClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertTrue(subject.formUiState.invalidEmail)
        Assert.assertFalse(subject.formUiState.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }
    }

    @Test
    fun `onRegistrationClick with invalid password causes early return`() {
        subject.setEmail(VALID_EMAIL)
        subject.setPassword(INVALID_PASSWORD)

        subject.onRegistrationClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertFalse(subject.formUiState.invalidEmail)
        Assert.assertTrue(subject.formUiState.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }
    }

    @Test
    fun `onRegistrationClick updates uiState with error when auth fails`() {
        givenValidCredentials()
        val authError = Error(0)
        coEvery { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers {
            Assert.assertTrue(subject.uiState.isLoading)
            authError
        }

        subject.onRegistrationClick()

        validateAuthError(subject.uiState)
        coVerify(exactly = 1) { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    @Test
    fun `onRegistrationClick updates uiState with success when auth succeeds`() {
        givenValidCredentials()
        coEvery { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers {
            Assert.assertTrue(subject.uiState.isLoading)
            Success
        }

        subject.onRegistrationClick()

        validateAuthSuccess(subject.uiState)
        coVerify(exactly = 1) { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    /**
     * Function to set valid auth credentials in subject
     */
    private fun givenValidCredentials() {
        subject.setEmail(VALID_EMAIL)
        subject.setPassword(VALID_PASSWORD)
    }

    /**
     * Function to validate initial values of subject's [AuthUiState]
     */
    private fun validateInitAuthUiState(state: AuthUiState) {
        Assert.assertFalse(state.isLoading)
        Assert.assertNull(state.errorMessage)
        Assert.assertFalse(state.isUserLoggedIn)
    }

    /**
     * Function to validate initial values of subject's [AuthFormUiState]
     */
    private fun validateInitAuthFormUiState(state: AuthFormUiState) {
        Assert.assertEquals("", state.email)
        Assert.assertEquals("", state.password)
        Assert.assertFalse(state.invalidEmail)
        Assert.assertFalse(state.invalidPassword)
    }

    /**
     * Function to validate failure auth result
     */
    private fun validateAuthError(state: AuthUiState) {
        Assert.assertNotNull(state.errorMessage)
        Assert.assertFalse(state.isUserLoggedIn)
    }

    /**
     * Function to validate successful auth result
     */
    private fun validateAuthSuccess(state: AuthUiState) {
        Assert.assertNull(state.errorMessage)
        Assert.assertTrue(state.isUserLoggedIn)
    }
}