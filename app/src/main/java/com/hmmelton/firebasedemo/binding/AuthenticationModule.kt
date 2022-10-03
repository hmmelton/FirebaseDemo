package com.hmmelton.firebasedemo.binding

import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.utils.AuthManager
import com.hmmelton.firebasedemo.utils.FirebaseAuthManager
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class AuthenticationModule {

    @Binds
    abstract fun provideAuthManager(authManager: FirebaseAuthManager): AuthManager

    companion object {
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}