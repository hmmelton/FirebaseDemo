package com.hmmelton.firebasedemo.data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.UserDatabaseQueryFailureEvent
import com.hmmelton.firebasedemo.data.model.User
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class UserRepositoryTest {

    @MockK
    lateinit var database: DatabaseReference
    @MockK
    lateinit var analytics: AnalyticsClient
    @InjectMockKs
    lateinit var subject: UserRepository

    private lateinit var task: Task<DataSnapshot>
    private lateinit var snapshot: DataSnapshot

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(Log::class)
        task = mockk()
        snapshot = mockk()

        every { database.child(any()) } returns database
        every { database.get() } returns task
        every { task.isComplete } returns true
        every { task.isCanceled } returns false
        every { task.result } returns snapshot
        every { task.exception } returns null
        every { Log.e(any(), any(), any()) } returns 0
        justRun { analytics.logEvent(any()) }
    }

    @After
    fun cleanUp() {
        clearAllMocks(objectMocks = false, staticMocks = false, constructorMocks = false)
    }

    @Test
    fun get_databaseReadThrowsError_returnNull() = runTest {
        val exception = FirebaseException("database error")
        every { task.exception } returns exception

        val user = subject.get("abc123")

        verify { analytics.logEvent(any<UserDatabaseQueryFailureEvent>()) }
        verify(exactly = 1) { Log.e(any(), any(), any<FirebaseException>()) }
        Assert.assertNull(user)
    }

    @Test
    fun get_snapshotDoesNotExist_returnNull() = runTest {
        every { snapshot.exists() } returns false

        val user = subject.get("abc123")

        verify(exactly = 1) { Log.e(any(), any()) }
        Assert.assertNull(user)
    }

    @Test
    fun get_snapshotThrowsClassCastException_returnNull() = runTest {
        val exception = ClassCastException()
        every { snapshot.exists() } returns true
        every { snapshot.getValue(User::class.java) } throws exception

        val user = subject.get("abc123")

        verify { snapshot.getValue(User::class.java) }
        verify { Log.e(any(), any(), any<ClassCastException>()) }
        Assert.assertNull(user)
    }

    @Test
    fun get_none_returnUser() = runTest {
        val databaseUser = mockk<User>()
        every { snapshot.exists() } returns true
        every { snapshot.getValue(User::class.java) } returns databaseUser

        val user = subject.get("abc123")

        Assert.assertEquals(databaseUser, user)
    }
}