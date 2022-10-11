package com.hmmelton.firebasedemo.data.model

sealed interface Response

object Success : Response
class Error(val message: String): Response