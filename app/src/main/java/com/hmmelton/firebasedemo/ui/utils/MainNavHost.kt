package com.hmmelton.firebasedemo.ui.utils

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hmmelton.firebasedemo.ui.screens.auth.AuthScreen
import com.hmmelton.firebasedemo.ui.screens.home.HomeRoute
import com.hmmelton.firebasedemo.ui.screens.auth.AuthViewModel
import com.hmmelton.firebasedemo.ui.screens.home.HomeViewModel
import com.hmmelton.firebasedemo.data.auth.AuthManager

/**
 * Composable for [com.hmmelton.firebasedemo.MainActivity] navigation host.
 */
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
    authManager: AuthManager
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            val viewModel = hiltViewModel<AuthViewModel>()
            AuthScreen(
                onAuthenticated = { navController.navigate(Routes.HOME) },
                viewModel = viewModel
            )
        }
        composable(Routes.HOME) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeRoute(viewModel = viewModel)
        }
    }

    // Listen for user to become unauthenticated, then navigate to login screen
    // TODO: check how this affects recomposition - need
    if (!authManager.observeAuthState().value) {
        val currentRoute = navController.currentDestination?.route

        // Done to prevent navigation during logout events on auth screen
        if (currentRoute != Routes.LOGIN) {
            navController.navigate(Routes.LOGIN) {
                // This removes the backstack
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }
}