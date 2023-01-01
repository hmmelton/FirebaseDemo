package com.hmmelton.firebasedemo.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.analytics.AnalyticsClient
import com.hmmelton.firebasedemo.analytics.events.RecipesDatabaseQueryFailureEvent
import com.hmmelton.firebasedemo.data.api.RecipeService
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.await
import javax.inject.Inject

private const val TAG = "MainViewModel"

/**
 * [ViewModel] used for the main screen.
 *
 * Note: Currently only used to sign user out, but will grow as more features are added.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val recipeService: RecipeService,
    private val dispatcher: CoroutineDispatcher,
    private val analytics: AnalyticsClient
) : ViewModel() {

    val recipes = mutableListOf<Recipe>()

    private var getRecipesJob: Job? = null

    /**
     * This function signs out the current user.
     */
    fun signOut() {
        authManager.signOut()
    }

    /**
     * This function fetches recipes from the remote database.
     * TODO(recipes data): update this data fetch process to cache recipes in Room db
     */
    suspend fun getRecipes() {
        // If previous call to function is still fetching data, ignore newer calls
        if (getRecipesJob != null) return

        getRecipesJob = viewModelScope.launch(dispatcher) {
            try {
                // Fetch latest recipes and update recipe list
                // TODO(recipes data): add more recipes and update this to use pagination
                val result = recipeService.getRecipes().await()
                recipes.clear()
                recipes.addAll(result)
            } catch (e: Exception) {
                // Log error to console and analytics
                Log.e(TAG, "error fetching recipes")
                analytics.logEvent(RecipesDatabaseQueryFailureEvent(e))
            }
        }

        getRecipesJob = null
    }
}