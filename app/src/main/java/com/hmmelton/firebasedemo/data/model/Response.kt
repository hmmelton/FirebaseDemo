package com.hmmelton.firebasedemo.data.model

import androidx.annotation.StringRes

sealed interface Response

object Success : Response
class Error(@StringRes val messageId: Int): Response