package com.hmmelton.firebasedemo.analytics.events

import android.os.Bundle
import androidx.core.os.bundleOf

private const val NAME = "thumbnail_uri_fetch_failure"

class ThumbnailUriFetchFailureEvent(
    private val e: Exception,
    private val imagePath: String
) : AnalyticsEvent(NAME) {

override fun getPropertiesBundle(): Bundle {
        return bundleOf(
            PropertyKeys.STACKTRACE to e.stackTraceToString(),
            PropertyKeys.IMAGE_PATH to imagePath
        )
    }
}