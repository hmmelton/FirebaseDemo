package com.hmmelton.firebasedemo.data.api

import com.hmmelton.firebasedemo.data.model.Recipe
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This API is used to fetch articles from the NYT API.
 * @see [NYT API](https://developer.nytimes.com/apis)
 */
interface RecipeService {
    @GET("?orderBy=\"category\"")
    suspend fun getRecipesForSection(
        @Query("equalTo") category: String
    ): List<Recipe>
}