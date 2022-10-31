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
        private const val AUTH_ERROR = "Auth error"
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
        val uiState = subject.uiState.value

        Assert.assertEquals("", email)
        Assert.assertEquals("", password)
        Assert.assertFalse(invalidEmail)
        Assert.assertFalse(invalidPassword)
        Assert.assertTrue(uiState == AuthUiState.AuthInit)
    }

    @Test
    fun `onSignInClick with empty email causes invalidEmail and early return`() = runTest {
        subject.onSignInClick()

        val uiState = subject.uiState.value
        Assert.assertTrue(uiState == AuthUiState.AuthInit)
        coVerify(exactly = 0) { authManager.signInWithEmail(any(), any()) }
    }

    @Test
    fun `onSignInClick updates uiState with error when auth fails`() = runTest {
        val uiState = subject.uiState
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
        val authError = Error(AUTH_ERROR)
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers  {
            Assert.assertEquals(AuthUiState.AuthLoading, uiState.value)
            authError
        }

        subject.onSignInClick()

        Assert.assertTrue(uiState.value is AuthUiState.AuthFailure)

        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }

    @Test
    fun `onSignInClick updates uiState with success when auth succeeds`() {
        val uiState = subject.uiState
        subject.email = VALID_EMAIL
        subject.password = VALID_PASSWORD
        coEvery { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) } answers {
            Assert.assertEquals(AuthUiState.AuthLoading, uiState.value)
            Success
        }

        subject.onSignInClick()

        Assert.assertEquals(AuthUiState.AuthSuccess, uiState.value)

        coVerify(exactly = 1) { authManager.signInWithEmail(VALID_EMAIL, VALID_PASSWORD) }
    }
}