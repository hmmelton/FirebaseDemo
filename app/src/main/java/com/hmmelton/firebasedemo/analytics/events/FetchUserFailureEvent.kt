package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "fetch_user_failure"

class FetchUserFailureEvent(private val isRegistration: Boolean) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.IS_REGISTRATION to isRegistration
        )
    }
}