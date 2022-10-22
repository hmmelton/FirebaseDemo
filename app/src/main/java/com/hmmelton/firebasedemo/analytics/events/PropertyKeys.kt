package com.hmmelton.firebasedemo.analytics.events

/**
 * Keys used to set properties in [AnalyticsEvent] subclasses.
 */
object PropertyKeys {
    const val STACKTRACE = "stacktrace"
    const val AUTH_TYPE = "auth_type"
    const val IS_REGISTRATION = "is_registration"
    const val ITEM_KEY = "item_key"

    // For testing
    const val TEST_PARAM = "test_param"
}

/**
 * Used to differentiate [AnalyticsEvent] by authentication provider
 *
 * Note: currently only email login/registration is set up
 */
enum class AuthType {
    EMAIL
}