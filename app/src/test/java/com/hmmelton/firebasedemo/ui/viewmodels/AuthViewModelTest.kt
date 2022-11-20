package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.core.util.PatternsCompat
import com.hmmelton.firebasedemo.MainDispatcherRule
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.utils.AuthManager
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
        val email = subject.email
        val password = subject.password
        val invalidEmail = subject.invalidEmail
        val invalidPassword = subject.invalidPassword
        val uiState = subject.uiState

        Assert.assertEquals("", email)
        Assert.assertEquals("", password)
        Assert.assertFalse(invalidEmail)
        Assert.assertFalse(invalidPassword)
        validateInitAuthUiState(uiState)
    }

    @Test
    fun `onSignInClick with invalid email causes early return`() {
        subject.onSignInClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertTrue(subject.invalidEmail)
        coVerify(exactly = 0) { authManager.signInWithEmail(any(), any()) }
    }

    @Test
    fun `onSignInClick updates uiState with error when auth fails`() {
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
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
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
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
        subject.password = VALID_PASSWORD

        subject.onRegistrationClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertTrue(subject.invalidEmail)
        Assert.assertFalse(subject.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }
    }

    @Test
    fun `onRegistrationClick with invalid password causes early return`() {
        subject.email = VALID_EMAIL
        subject.password = INVALID_PASSWORD

        subject.onRegistrationClick()

        validateInitAuthUiState(subject.uiState)
        Assert.assertFalse(subject.invalidEmail)
        Assert.assertTrue(subject.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }
    }

    @Test
    fun `onRegistrationClick updates uiState with error when auth fails`() {
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
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
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
        coEvery { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers {
            Assert.assertTrue(subject.uiState.isLoading)
            Success
        }

        subject.onRegistrationClick()

        validateAuthSuccess(subject.uiState)
        coVerify(exactly = 1) { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    private fun validateInitAuthUiState(state: AuthUiState) {
        Assert.assertFalse(state.isLoading)
        Assert.assertNull(state.errorMessage)
        Assert.assertFalse(state.isUserLoggedIn)
    }

    private fun validateAuthError(state: AuthUiState) {
        Assert.assertNotNull(state.errorMessage)
        Assert.assertFalse(state.isUserLoggedIn)
    }

    private fun validateAuthSuccess(state: AuthUiState) {
        Assert.assertNull(state.errorMessage)
        Assert.assertTrue(state.isUserLoggedIn)
    }
}