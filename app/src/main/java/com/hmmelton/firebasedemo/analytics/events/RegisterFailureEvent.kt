package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "RegisterFailureEvent"

/**
 * [AnalyticsEvent] used for user registration failures.
 */
class RegisterFailureEvent(
    private val e: Exception,
    private val authType: AuthType
) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTrace,
            PropertyKeys.AUTH_TYPE to authType
        )
    }
}