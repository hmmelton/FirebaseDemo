package com.hmmelton.firebasedemo.analytics

import com.hmmelton.firebasedemo.analytics.events.FetchUserFailureEvent
import com.hmmelton.firebasedemo.analytics.events.PropertyKeys
import com.hmmelton.firebasedemo.analytics.events.UserDatabaseQueryFailureEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserDatabaseEventsTest {

    @Test
    fun getPropertiesBundle_fetchUserFailureEvent_correctBundle() {
        val eventFromReg = FetchUserFailureEvent(true)
        val eventNotFromReg = FetchUserFailureEvent(false)

        val bundleFromReg = eventFromReg.getPropertiesBundle()
        val bundleNotFromReg = eventNotFromReg.getPropertiesBundle()

        Assert.assertEquals(1, bundleFromReg.size())
        Assert.assertEquals(true, bundleFromReg.getBoolean(PropertyKeys.IS_REGISTRATION))
        Assert.assertEquals(1, bundleNotFromReg.size())
        Assert.assertEquals(
            false,
            bundleNotFromReg.getBoolean(PropertyKeys.IS_REGISTRATION)
        )
    }

    @Test
    fun getPropertiesBundle_userDatabaseQueryFailureEvent_correctBundle() {
        val stacktrace = "TestClass.testMethod(Unknown Source)"
        val exception = mockk<Exception>()
        mockkStatic(Throwable::stackTraceToString)
        every { exception.stackTraceToString() } returns stacktrace
        val event = UserDatabaseQueryFailureEvent("test_key", exception)

        val bundle = event.getPropertiesBundle()

        Assert.assertEquals(2, bundle.size())
        Assert.assertEquals("test_key", bundle.getString(PropertyKeys.ITEM_KEY))
        Assert.assertEquals(stacktrace, bundle.getString(PropertyKeys.STACKTRACE))
    }
}