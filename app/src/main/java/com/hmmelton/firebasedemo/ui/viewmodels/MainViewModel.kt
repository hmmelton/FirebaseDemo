package com.hmmelton.firebasedemo.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.data.model.RecipeListItem
import com.hmmelton.firebasedemo.data.repository.RecipeRepository
import com.hmmelton.firebasedemo.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    private val repository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    var recipes = mutableStateOf<List<RecipeListItem>>(emptyList())
        private set
    val categories = repository.getCategories()

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
    fun getRecipes() {
        // If previous call to function is still fetching data, ignore newer calls
        if (getRecipesJob != null) return

        getRecipesJob = viewModelScope.launch(dispatcher) {
            // Fetch latest recipes and update recipe list
            // TODO(recipes data): add more recipes and update this to use pagination
            val result = repository.getAll()
            Log.d(TAG, "Recipes num: ${result?.size}")

            // To avoid clearing data in the case of a database issue, only update data when query
            // result is not null
            result?.let { latestRecipes ->
                recipes.value = latestRecipes
            }
        }

        getRecipesJob = null
    }
}