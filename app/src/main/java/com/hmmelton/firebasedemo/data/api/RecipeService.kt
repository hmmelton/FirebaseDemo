package com.hmmelton.firebasedemo.data.api

import com.hmmelton.firebasedemo.data.model.Recipe
import retrofit2.Call
import retrofit2.http.GET

/**
 * This API is used to fetch recipes from the app's Firebase Realtime Database.
 */
interface RecipeService {
    @GET
    suspend fun getRecipes(): Call<List<Recipe>>
}