package com.hmmelton.firebasedemo.analytics

import android.os.Bundle

/**
 * Base class for analytics events.
 */
abstract class AnalyticsEvent(val name: String) {

    /**
     * This function should be used to convert any event-specific properties to a [Bundle].
     */
    abstract fun getPropertiesBundle(): Bundle
}