package com.hmmelton.firebasedemo.analytics

/**
 * Interface for analytics logging file
 */
interface AnalyticsClient {
    fun logEvent(event: AnalyticsEvent)
}