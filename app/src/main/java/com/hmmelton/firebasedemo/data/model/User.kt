package com.hmmelton.firebasedemo.data.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Class representing user, independent of auth provider
 */
@IgnoreExtraProperties
data class User(
    val email: String? = null,
    val username: String? = null
)
