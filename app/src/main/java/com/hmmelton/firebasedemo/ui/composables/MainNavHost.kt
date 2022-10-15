package com.hmmelton.firebasedemo.ui.composables

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.ui.composables.screens.AuthScreen
import com.hmmelton.firebasedemo.ui.composables.screens.HomeScreen
import com.hmmelton.firebasedemo.ui.viewmodels.AuthViewModel
import com.hmmelton.firebasedemo.ui.viewmodels.MainViewModel
import com.hmmelton.firebasedemo.utils.Routes

/**
 * Composable for [com.hmmelton.firebasedemo.MainActivity] navigation host.
 */
@Composable
fun MainNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            val viewModel = hiltViewModel<AuthViewModel>()
            AuthScreen(
                onAuthenticated = { navController.navigate(Routes.HOME) },
                viewModel = viewModel
            )
        }
        composable(Routes.HOME) {
            val viewModel = hiltViewModel<MainViewModel>()
            HomeScreen(
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        // This removes the backstack
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }
    }

    // Listen for user to become unauthenticated, then navigate to login screen
    FirebaseAuth.getInstance().addAuthStateListener { auth ->
        if (auth.currentUser == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }
}