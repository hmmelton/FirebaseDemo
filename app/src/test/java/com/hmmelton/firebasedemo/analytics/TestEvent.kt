package com.hmmelton.firebasedemo.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.hmmelton.firebasedemo.analytics.events.AnalyticsEvent
import com.hmmelton.firebasedemo.analytics.events.PropertyKeys

class TestEvent(private val testParam: String) : AnalyticsEvent(NAME) {

    companion object {
        const val NAME = "test_event"
    }

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.TEST_PARAM to testParam
        )
    }
}