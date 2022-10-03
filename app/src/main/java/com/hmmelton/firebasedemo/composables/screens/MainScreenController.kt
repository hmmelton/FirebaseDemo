package com.hmmelton.firebasedemo.composables.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue

import com.hmmelton.firebasedemo.utils.AuthManager

@Composable
fun MainScreenController(auth: AuthManager) {
    val isAuthenticated by remember { auth.getAuthenticationState() }
    if (isAuthenticated) {
        HomeScreen(auth)
    } else {
        AuthScreen(auth)
    }
}