package com.hmmelton.firebasedemo.binding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hmmelton.firebasedemo.data.model.User
import com.hmmelton.firebasedemo.data.repository.Repository
import com.hmmelton.firebasedemo.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun provideUserRepository(repository: UserRepository): Repository<User>

    companion object {
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