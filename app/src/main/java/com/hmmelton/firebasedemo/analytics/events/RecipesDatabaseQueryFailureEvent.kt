package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "recipes_database_query_failure"

class RecipesDatabaseQueryFailureEvent(private val e: Exception) : AnalyticsEvent(NAME) {
    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTraceToString()
        )
    }
}