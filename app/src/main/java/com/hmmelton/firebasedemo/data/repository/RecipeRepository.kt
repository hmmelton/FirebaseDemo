package com.hmmelton.firebasedemo.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.RecipeParseFailureEvent
import com.hmmelton.firebasedemo.analytics.events.RecipesDatabaseQueryFailureEvent
import com.hmmelton.firebasedemo.analytics.events.ThumbnailUriFetchFailureEvent
import com.hmmelton.firebasedemo.binding.Recipes
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.data.model.RecipeListItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "RecipeRepository"

/**
 * This class is a data repository used to interact with [Recipe] data.
 */
class RecipeRepository @Inject constructor(
    @Recipes private val database: DatabaseReference,
    private val storage: StorageReference,
    private val analytics: AnalyticsClient
) {

    /**
     * This function retrieves all stored [Recipe] objects. There is currently no support for
     * filtering or pagination.
     *
     * @return List of all stored Recipes
     */
    suspend fun getAll(): List<RecipeListItem>? {
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

        val result = mutableListOf<RecipeListItem>()

        // Results must be deserialized individually
        for (recipeSnapshot in snapshot.children) {
            // First, attempt to parse Recipe from snapshot
            val recipe = try {
                recipeSnapshot.getValue(Recipe::class.java)
            } catch (e: Exception) {
                Log.e(TAG, "error converting fetched value to Recipe object", e)
                analytics.logEvent(RecipeParseFailureEvent(e))
                null
            } ?: continue

            Log.d(TAG, recipe.toString())

            // If recipe was not null, attempt to fetch thumbnail image URI
            val thumbnailUrl = try {
                // TODO(recipes): resize thumbnail images
                storage.child(recipe.photoMainUri).downloadUrl.await()
            } catch (e: Exception) {
                Log.e(TAG, "error fetching thumbnail image", e)
                analytics.logEvent(ThumbnailUriFetchFailureEvent(e, recipe.photoThumbnailUri))
                null
            }

            result.add(RecipeListItem(recipe, thumbnailUrl.toString()))
        }

        return result
    }
}