package com.hmmelton.firebasedemo.composables.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.hmmelton.firebasedemo.data.model.Success

import com.hmmelton.firebasedemo.utils.AuthManager
import kotlinx.coroutines.launch

@Composable
fun MainScreenController(auth: AuthManager) {
    var isAuthenticated by remember { mutableStateOf(auth.isAuthenticated) }
    if (isAuthenticated) {
        HomeScreen(
            onSignOutClick = {
                auth.signOut()
                isAuthenticated = false
            }
        )
    } else {
        val composableScope = rememberCoroutineScope()
        AuthScreen(
            onSignInClick = { email, password ->
                composableScope.launch {
                    val response = auth.signInWithEmail(email, password)
                    if (response == Success) {
                        isAuthenticated = true
                    }
                }
            },
            onRegisterClick = { email, password ->
                composableScope.launch {
                    val response = auth.registerWithEmail(email, password)
                    if (response == Success) {
                        isAuthenticated = true
                    }
                }
            }
        )
    }
}