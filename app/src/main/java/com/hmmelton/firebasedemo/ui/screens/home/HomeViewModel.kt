package com.hmmelton.firebasedemo.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.data.repository.RecipeRepository
import com.hmmelton.firebasedemo.utils.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"

/**
 * UI state for the Home screen.
 */
sealed interface HomeUiState {
    /**
     * Loading state.
     *
     * This is the initial state of the screen, before any data is fetched.
     */
    object Loading : HomeUiState

    /**
     * Recipes have been fetched.
     *
     * This is the state achieved after successfully querying [Recipe] data from its repository.
     */
    data class Success(
        val recipes: List<Recipe>,
        val categories: List<RecipeCategory>
    ) : HomeUiState

    /**
     * Recipes were not fetched.
     *
     * This state occurs when there is an error fetching [Recipe] data.
     */
    data class Error(val message: Int) : HomeUiState
}

/**
 * [ViewModel] used for the main screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val repository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            HomeUiState.Loading
        )

    init {
        getRecipes()
    }

    var recipes = mutableStateOf<List<Recipe>>(emptyList())
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
     */
    fun getRecipes() {
        // If previous call to function is still fetching data, ignore newer calls
        if (getRecipesJob != null) return

        _uiState.update { HomeUiState.Loading }

        getRecipesJob = viewModelScope.launch(dispatcher) {
            // Fetch latest recipes and update recipe list
            // TODO(recipes data): add more recipes and update this to use pagination
            val result = repository.getAll()
            Log.d(TAG, "Recipes num: ${result?.size}")

            // Update UI state with result of query
            if (result != null) {
                _uiState.update { HomeUiState.Success(result) }
            } else {
                _uiState.update { HomeUiState.Error(R.string.recipe_query_error) }
            }
        }

        getRecipesJob = null
    }
}