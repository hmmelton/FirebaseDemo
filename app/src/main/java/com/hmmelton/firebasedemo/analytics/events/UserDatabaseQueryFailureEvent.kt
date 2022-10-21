package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "user_database_query_failure"

class UserDatabaseQueryFailureEvent(
    private val itemKey: String,
    private val e: Exception
) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.ITEM_KEY to itemKey,
            PropertyKeys.STACKTRACE to e.stackTrace.toString()
        )
    }
}