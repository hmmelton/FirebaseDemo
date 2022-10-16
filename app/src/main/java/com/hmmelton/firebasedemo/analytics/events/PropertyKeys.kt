package com.hmmelton.firebasedemo.analytics.events

/**
 * Keys used to set properties in [AnalyticsEvent] subclasses.
 */
object PropertyKeys {
    const val STACKTRACE = "stacktrace"
    const val AUTH_TYPE = "auth_type"
}

/**
 * Used to differentiate [AnalyticsEvent] by authentication provider
 *
 * Note: currently only email login/registration is set up
 */
enum class AuthType {
    EMAIL
}