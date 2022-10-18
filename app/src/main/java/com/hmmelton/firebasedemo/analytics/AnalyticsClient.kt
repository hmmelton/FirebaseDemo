package com.hmmelton.firebasedemo.analytics

import com.hmmelton.firebasedemo.analytics.events.AnalyticsEvent

/**
 * Interface for analytics logging file
 */
interface AnalyticsClient {
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(id: String)
}