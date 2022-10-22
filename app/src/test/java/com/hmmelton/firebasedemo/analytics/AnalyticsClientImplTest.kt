package com.hmmelton.firebasedemo.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.hmmelton.firebasedemo.analytics.events.PropertyKeys
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AnalyticsClientImplTest {

    companion object {
        private lateinit var firebaseAnalytics: FirebaseAnalytics
        private lateinit var subject: AnalyticsClientImpl

        @BeforeClass
        @JvmStatic
        fun setUp() {
            firebaseAnalytics = mockk()
            subject = AnalyticsClientImpl(firebaseAnalytics)

            justRun { firebaseAnalytics.logEvent(any(), any()) }
        }
    }

    @Test
    fun logEvent_isTestEvent_logsEventCorrectly() {
        val event = TestEvent("test_value")
        val captor = slot<Bundle>()
        val expectedValue = "test_value"

        subject.logEvent(event)

        verify { firebaseAnalytics.logEvent(TestEvent.NAME, capture(captor)) }

        val params = captor.captured
        Assert.assertEquals(1, params.size())
        Assert.assertEquals(expectedValue, params.getString(PropertyKeys.TEST_PARAM))
    }
}