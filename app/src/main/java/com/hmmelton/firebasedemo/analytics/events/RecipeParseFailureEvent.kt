package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "recipe_parse_failure"

class RecipeParseFailureEvent(private val e: Exception) : AnalyticsEvent(NAME) {

    override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTraceToString()
        )
    }
}