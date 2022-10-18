package com.hmmelton.firebasedemo.binding

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.AnalyticsClientImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Singleton
    @Binds
    abstract fun provideAnalyticsClient(analyticsClient: AnalyticsClientImpl): AnalyticsClient

    companion object {
        @Singleton
        @Provides
        fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

        @Singleton
        @Provides
        fun provideFirebaseDatabaseReference(): DatabaseReference {
            val database = Firebase.database
            // Persist data locally
            database.setPersistenceEnabled(true)
            // According to documentation, 1MB is the min cache size
            // https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase#setPersistenceCacheSizeBytes(long)
            database.setPersistenceCacheSizeBytes(1024 * 1024)

            return database.reference
        }
    }
}