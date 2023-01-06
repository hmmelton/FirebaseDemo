package com.hmmelton.firebasedemo.binding

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hmmelton.firebasedemo.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private val database by lazy {
        val db = Firebase.database
        // Persist data locally
        db.setPersistenceEnabled(true)
        // According to documentation, 1MB is the min cache size
        // https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase#setPersistenceCacheSizeBytes(long)
        db.setPersistenceCacheSizeBytes(1024 * 1024)

        if (BuildConfig.DEBUG) {
            db.useEmulator("10.0.2.2", 9000)
        }

        db
    }

    @Singleton
    @Provides
    @Users
    fun provideUsersDatabaseReference() = database.reference.child("users")

    @Singleton
    @Provides
    @Recipes
    fun provideRecipesDatabaseReference() = database.reference.child("recipes")

    @Singleton
    @Provides
    fun provideFileStorageReference() = Firebase.storage.reference
}
