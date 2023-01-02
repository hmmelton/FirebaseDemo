package com.hmmelton.firebasedemo.utils

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.FetchUserFailureEvent
import com.hmmelton.firebasedemo.analytics.events.SignInFailureEvent
import com.hmmelton.firebasedemo.data.model.Error
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.data.model.User
import io.mockk.CapturingSlot
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AuthManagerImplTest {

    companion object {
        private const val EMAIL = "name@email.com"
        private const val PASSWORD = "password"
    }

    @MockK
    lateinit var auth: FirebaseAuth
    @MockK
    lateinit var userRepository: Repository<User>
    @MockK
    lateinit var analytics: AnalyticsClient

    lateinit var subject: AuthManagerImpl

    lateinit var authListenerSlot: CapturingSlot<FirebaseAuth.AuthStateListener>
    lateinit var task: Task<AuthResult>
    lateinit var authResult: AuthResult
    lateinit var authUser: FirebaseUser

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(Log::class)
        authListenerSlot = slot()
        task = mockk()
        authResult = mockk()
        authUser = mockk()

        every { auth.currentUser } returns null
        every { auth.signInWithEmailAndPassword(EMAIL, PASSWORD) } returns task
        every { auth.createUserWithEmailAndPassword(EMAIL, PASSWORD) } returns task
        justRun { auth.addAuthStateListener(capture(authListenerSlot)) }
        every { authUser.uid } returns "abc123"
        every { authResult.user } returns authUser
        every { task.isComplete } returns true
        every { task.exception } returns null
        every { task.isCanceled } returns false
        every { task.result } returns authResult

        subject = AuthManagerImpl(auth, userRepository, analytics)
    }

    // Section: isAuthenticated

    @Test
    fun isAuthenticated_nullUser_false() {
        val isAuthenticated = subject.isAuthenticated()

        Assert.assertFalse(isAuthenticated)
    }

    @Test
    fun isAuthenticated_nonNullUser_true() {
        val user = mockk<FirebaseUser>()
        every { auth.currentUser } returns user

        val isAuthenticated = subject.isAuthenticated()

        Assert.assertTrue(isAuthenticated)
    }

    // Section: observeAuthState

    @Test
    fun observeAuthState_userBecomesAuthenticated_trueValueObserved() {
        val listener = authListenerSlot.captured
        val user = mockk<FirebaseUser>()

        val observedState = subject.observeAuthState()

        Assert.assertFalse(observedState.value)

        every { auth.currentUser } returns user

        listener.onAuthStateChanged(auth)

        Assert.assertTrue(observedState.value)
    }

    @Test
    fun observeAuthState_userBecomesUnauthenticated_falseValueObserved() {
        val listener = authListenerSlot.captured
        val user = mockk<FirebaseUser>()
        every { auth.currentUser } returns user

        // Must make this call because value is initialized to false due to null currentUser
        listener.onAuthStateChanged(auth)
        val observedState = subject.observeAuthState()

        Assert.assertTrue(observedState.value)

        every { auth.currentUser } returns null

        listener.onAuthStateChanged(auth)

        Assert.assertFalse(observedState.value)
    }

    // Section: signInWithEmail

    @Test
    fun signInWithEmail_exceptionThrown_returnError() = runTest {
        val exception = Exception()
        every { task.exception } returns exception
        every { auth.signInWithEmailAndPassword(EMAIL, PASSWORD) } throws exception

        val result = subject.signInWithEmail(EMAIL, PASSWORD)

        verify { Log.e(any(), any(), exception) }
        verify { analytics.logEvent(any<SignInFailureEvent>()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun signInWithEmail_resultUserNull_returnError() = runTest {
        every { authResult.user } returns null

        val result = subject.signInWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify(exactly = 0) { analytics.logEvent(any()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun signInWithEmail_userRepositoryReturnsNull_returnError() = runTest {
        coEvery { userRepository.get(any()) } returns null
        justRun { auth.signOut() }

        val result = subject.signInWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify { auth.signOut() }
        verify { analytics.logEvent(any<FetchUserFailureEvent>()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun signInWithEmail_none_returnUser() = runTest {
        val user = mockk<User>()
        coEvery { userRepository.get(any()) } returns user
        justRun { analytics.setUserId(any()) }

        val result = subject.signInWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify(exactly = 0) { analytics.logEvent(any()) }
        verify { analytics.setUserId(any()) }
        Assert.assertTrue(result == Success)
    }

    // Section: registerWithEmail

    @Test
    fun registerWithEmail_exceptionThrown_returnError() = runTest {
        val exception = Exception()
        every { task.exception } returns exception
        every { auth.createUserWithEmailAndPassword(EMAIL, PASSWORD) } throws exception

        val result = subject.registerWithEmail(EMAIL, PASSWORD)

        verify { Log.e(any(), any(), exception) }
        verify { analytics.logEvent(any<SignInFailureEvent>()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun registerWithEmail_resultUserNull_returnError() = runTest {
        every { authResult.user } returns null

        val result = subject.registerWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify(exactly = 0) { analytics.logEvent(any()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun registerWithEmail_userRepositoryReturnsNull_returnError() = runTest {
        coEvery { userRepository.get(any()) } returns null
        justRun { auth.signOut() }

        val result = subject.registerWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify { auth.signOut() }
        verify { analytics.logEvent(any<FetchUserFailureEvent>()) }
        Assert.assertTrue(result is Error)
    }

    @Test
    fun registerWithEmail_none_returnUser() = runTest {
        val user = mockk<User>()
        coEvery { userRepository.get(any()) } returns user
        justRun { analytics.setUserId(any()) }

        val result = subject.registerWithEmail(EMAIL, PASSWORD)

        verify(exactly = 0) { Log.e(any(), any(), any()) }
        verify(exactly = 0) { analytics.logEvent(any()) }
        verify { analytics.setUserId(any()) }
        Assert.assertTrue(result == Success)
    }
}