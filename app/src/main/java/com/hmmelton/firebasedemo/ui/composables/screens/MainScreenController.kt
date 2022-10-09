package com.hmmelton.firebasedemo.ui.composables.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.hmmelton.firebasedemo.data.model.Success
import com.hmmelton.firebasedemo.utils.AuthManager
import kotlinx.coroutines.launch

private const val TAG = "MainScreenController"

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
                    } else {
                        // TODO: show dialog
                    }
                }
            },
            onRegisterClick = { email, password ->
                Log.i(TAG, "in onRegisterClick")
                composableScope.launch {
                    val response = auth.registerWithEmail(email, password)
                    Log.i(TAG, "registration response: $response")
                    when (response) {
                        Success -> isAuthenticated = true
                        is Error -> Log.e(TAG, response.message ?: "registration error")
                        else -> Log.e(TAG, "unknown response $response")
                    }
                }
            }
        )
    }
}