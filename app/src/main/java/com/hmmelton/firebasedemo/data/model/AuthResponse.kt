package com.hmmelton.firebasedemo.data.model

import androidx.annotation.StringRes

sealed interface AuthResponse

object Success : AuthResponse
class Error(@StringRes val messageId: Int): AuthResponse