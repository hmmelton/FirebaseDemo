package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "sign_in_failure"

/**
 * [AnalyticsEvent] used for user sign in failures.
 */
class SignInFailureEvent(
    private val e: Exception,
    private val authType: AuthType
) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTrace.toString(),
            PropertyKeys.AUTH_TYPE to authType
        )
    }
}