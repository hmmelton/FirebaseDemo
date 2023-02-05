package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.core.util.PatternsCompat
import com.hmmelton.firebasedemo.MainDispatcherRule
import com.hmmelton.firebasedemo.data.auth.AuthManager
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.ui.screens.auth.AuthUiState
import com.hmmelton.firebasedemo.ui.screens.auth.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
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
        val uiState = subject.uiState.value

        Assert.assertFalse(uiState.isLoading)
        Assert.assertNull(uiState.errorMessage)
        Assert.assertFalse(uiState.isUserLoggedIn)
        Assert.assertEquals("", uiState.email)
        Assert.assertEquals("", uiState.password)
        Assert.assertFalse(uiState.invalidEmail)
        Assert.assertFalse(uiState.invalidPassword)
    }

    @Test
    fun `onSignInClick with invalid email causes early return`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        subject.onSignInClick()

        Assert.assertEquals(2, uiStates.size)
        Assert.assertTrue(uiStates.last().invalidEmail)
        coVerify(exactly = 0) { authManager.signInWithEmail(any(), any()) }

        collectJob.cancel()
    }

    @Test
    fun `onSignInClick updates uiState with error when auth fails`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        givenValidCredentials()
        val authError = Error(0)
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers  { authError }

        subject.onSignInClick()

        Assert.assertEquals(4, uiStates.size)
        validateAuthError(uiStates.last())
        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }

        collectJob.cancel()
    }

    @Test
    fun `onSignInClick updates uiState with success when auth succeeds`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        givenValidCredentials()
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers { Success }

        subject.onSignInClick()

        Assert.assertEquals(4, uiStates.size)
        validateAuthSuccess(uiStates.last())
        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }

        collectJob.cancel()
    }

    @Test
    fun `onRegistrationClick with invalid email causes early return`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        // Set to ensure failure is from bad email
        subject.setPassword(VALID_PASSWORD)

        subject.onRegistrationClick()

        val uiState = uiStates.last()
        Assert.assertEquals(3, uiStates.size)
        Assert.assertTrue(uiState.invalidEmail)
        Assert.assertFalse(uiState.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }

        collectJob.cancel()
    }

    @Test
    fun `onRegistrationClick with invalid password causes early return`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        subject.setEmail(VALID_EMAIL)
        subject.setPassword(INVALID_PASSWORD)

        subject.onRegistrationClick()

        val uiState = uiStates.last()
        Assert.assertEquals(4, uiStates.size)
        Assert.assertFalse(uiState.invalidEmail)
        Assert.assertTrue(uiState.invalidPassword)
        coVerify(exactly = 0) { authManager.registerWithEmail(any(), any()) }

        collectJob.cancel()
    }

    @Test
    fun `onRegistrationClick updates uiState with error when auth fails`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        givenValidCredentials()
        val authError = Error(0)
        coEvery { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers { authError }

        subject.onRegistrationClick()

        Assert.assertEquals(4, uiStates.size)
        validateAuthError(uiStates.last())
        coVerify(exactly = 1) { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) }

        collectJob.cancel()
    }

    @Test
    fun `onRegistrationClick updates uiState with success when auth succeeds`() = runTest {
        val uiStates = mutableListOf<AuthUiState>()
        val collectJob = launch(mainDispatcherRule.testDispatcher) {
            subject.uiState.toCollection(uiStates)
        }

        givenValidCredentials()
        coEvery { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers { Success }

        subject.onRegistrationClick()

        Assert.assertEquals(4, uiStates.size)
        validateAuthSuccess(uiStates.last())
        coVerify(exactly = 1) { authManager.registerWithEmail(VALID_EMAIL, VALID_PASSWORD) }

        collectJob.cancel()
    }

    /**
     * Function to set valid auth credentials in subject
     */
    private fun givenValidCredentials() {
        subject.setEmail(VALID_EMAIL)
        subject.setPassword(VALID_PASSWORD)
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