package com.hmmelton.firebasedemo.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.hmmelton.firebasedemo.analytics.events.AnalyticsEvent
import javax.inject.Inject

/**
 * Implementation of [AnalyticsClient] used to log events to Firebase.
 */
class AnalyticsClientImpl @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsClient {

    override fun logEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.name, event.getPropertiesBundle())
    }

    override fun setUserId(id: String) {
        analytics.setUserId(id)
    }
}