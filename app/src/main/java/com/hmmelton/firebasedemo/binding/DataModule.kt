package com.hmmelton.firebasedemo.binding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hmmelton.firebasedemo.data.repository.RecipeRepository
import com.hmmelton.firebasedemo.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    @Users
    fun provideUsersDatabaseReference(): DatabaseReference {
        return Firebase.database.reference.child("users")
    }

    @Singleton
    @Provides
    @Recipes
    fun provideRecipesDatabaseReference(): DatabaseReference {
        return Firebase.database.reference.child("recipes")
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class Users

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class Recipes
