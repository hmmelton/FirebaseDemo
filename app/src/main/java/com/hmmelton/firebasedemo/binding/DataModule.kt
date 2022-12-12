package com.hmmelton.firebasedemo.binding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.hmmelton.firebasedemo.data.api.RecipeService
import com.hmmelton.firebasedemo.data.model.User
import com.hmmelton.firebasedemo.data.repository.Repository
import com.hmmelton.firebasedemo.data.repository.UserRepository
import com.hmmelton.firebasedemo.utils.Keys
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

        @Singleton
        @Provides
        fun provideRecipeService(): RecipeService {
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl(Keys.RECIPES_DB_ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(RecipeService::class.java)
        }
    }
}