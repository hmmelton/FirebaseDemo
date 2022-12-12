package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hmmelton.firebasedemo.data.api.RecipeService
import com.hmmelton.firebasedemo.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [ViewModel] used for the main screen.
 *
 * Note: Currently only used to sign user out, but will grow as more features are added.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val recipeService: RecipeService
) : ViewModel() {

    fun signOut() {
        authManager.signOut()
    }
}