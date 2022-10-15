package com.hmmelton.firebasedemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.hmmelton.firebasedemo.utils.AuthManager
import javax.inject.Inject

/**
 * [ViewModel] used for the main screen.
 *
 * Note: Currently only used to sign user out, but will grow as more features are added.
 */
class MainViewModel @Inject constructor(private val authManager: AuthManager) : ViewModel() {

    fun signOut() {
        authManager.signOut()
    }
}