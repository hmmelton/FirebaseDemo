package com.hmmelton.firebasedemo.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.RecipesDatabaseQueryFailureEvent
import com.hmmelton.firebasedemo.binding.Recipes
import com.hmmelton.firebasedemo.data.model.Recipe
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "RecipeRepository"

/**
 * This class is a data repository used to interact with [Recipe] data.
 */
class RecipeRepository @Inject constructor(
    @Recipes private val database: DatabaseReference,
    private val analytics: AnalyticsClient
) {

    /**
     * This function retrieves all stored [Recipe] objects. There is currently no support for
     * filtering or pagination.
     *
     * @return List of all stored Recipes
     */
    suspend fun getAll(): List<Recipe>? {
        val snapshot = try {
            database.get().await()
        } catch (e: Exception) {
            Log.e(TAG, "error fetching Recipes", e)
            analytics.logEvent(RecipesDatabaseQueryFailureEvent(e))
            return null
        }

        if (!snapshot.exists()) {
            Log.e(TAG, "could not find Recipes")
            return null
        }

        val result = mutableListOf<Recipe>()

        // Results must be deserialized individually
        for (recipeSnapshot in snapshot.children) {
            val recipe = try {
                recipeSnapshot.getValue(Recipe::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "error converting fetched value to Recipe object", e)
                null
            }

            Log.d(TAG, "Recipe ID: ${recipe?.id}")

            if (recipe != null) result.add(recipe)
        }

        return result
    }
}