package com.hmmelton.firebasedemo.analytics

import com.hmmelton.firebasedemo.analytics.events.AuthType
import com.hmmelton.firebasedemo.analytics.events.PropertyKeys
import com.hmmelton.firebasedemo.analytics.events.RegistrationFailureEvent
import com.hmmelton.firebasedemo.analytics.events.SignInFailureEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthenticationAnalyticsEventsTest {

    companion object {
        private lateinit var exception: Exception

        private const val STACKTRACE = "TestClass.testMethod(Unknown Source)"

        @BeforeClass
        @JvmStatic
        fun setUp() {
            mockkStatic(Throwable::stackTraceToString)
            exception = mockk()
            every { exception.stackTraceToString() } returns STACKTRACE
        }
    }

    @Test
    fun getPropertiesBundle_registrationFailureEvent_correctBundle() {
        val event = RegistrationFailureEvent(exception, AuthType.EMAIL)

        val bundle = event.getPropertiesBundle()

        Assert.assertEquals(2, bundle.size())
        Assert.assertEquals(STACKTRACE, bundle.getString(PropertyKeys.STACKTRACE))
        Assert.assertEquals(
            AuthType.EMAIL,
            bundle.getSerializable(PropertyKeys.AUTH_TYPE) as? AuthType
        )
    }

    @Test
    fun getPropertiesBundle_signInFailureEvent_correctBundle() {
        val event = SignInFailureEvent(exception, AuthType.EMAIL)

        val bundle = event.getPropertiesBundle()

        Assert.assertEquals(2, bundle.size())
        Assert.assertEquals(STACKTRACE, bundle.getString(PropertyKeys.STACKTRACE))
        Assert.assertEquals(
            AuthType.EMAIL,
            bundle.getSerializable(PropertyKeys.AUTH_TYPE) as? AuthType
        )
    }
}