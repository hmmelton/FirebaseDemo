package com.hmmelton.firebasedemo.binding

import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.firebasedemo.BuildConfig
import com.hmmelton.firebasedemo.utils.AuthManager
import com.hmmelton.firebasedemo.utils.FirebaseAuthManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationModule {

    @Singleton
    @Binds
    abstract fun provideAuthManager(authManager: FirebaseAuthManager): AuthManager

    companion object {
        @Singleton
        @Provides
        fun provideFirebaseAuth(): FirebaseAuth {
            val auth = FirebaseAuth.getInstance()
            if (BuildConfig.DEBUG) {
                auth.useEmulator("10.0.2.2", 9099)
            }
            return auth
        }

        @Singleton
        @Provides
        fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}