package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "registration_failure"

/**
 * [AnalyticsEvent] used for user registration failures.
 */
class RegistrationFailureEvent(
    private val e: Exception,
    private val authType: AuthType
) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTraceToString(),
            PropertyKeys.AUTH_TYPE to authType
        )
    }
}