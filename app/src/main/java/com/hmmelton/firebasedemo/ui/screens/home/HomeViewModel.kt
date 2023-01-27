package com.hmmelton.firebasedemo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.data.auth.AuthManager
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Home screen.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val categories: List<RecipeCategory> = emptyList(),
    val errorMessages: List<Int> = emptyList()
)

/**
 * [ViewModel] used for the main screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val repository: RecipeRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            HomeUiState(isLoading = true)
        )

    init {
        // Fetch recipes on initialization
        getRecipes()
    }

    private var getRecipesJob: Job? = null

    /**
     * This function signs out the current user.
     */
    fun signOut() {
        authManager.signOut()
    }

    /**
     * This function removes an error message from the UI state.
     */
    fun removeErrorMessage(message: Int) {
        _uiState.update {
            it.copy(errorMessages = it.errorMessages - message)
        }
    }

    fun onRecipeClicked(recipe: Recipe) {
        // TODO: navigate to recipe details screen
    }

    fun onCategoryClicked(category: RecipeCategory) {
        // TODO: navigate to category screen
    }

    /**
     * This function fetches recipes from the remote database.
     *
     * TODO(recipes data): call this function when user does manual refresh
     */
    fun getRecipes() {
        // If previous call to function is still fetching data, ignore newer calls
        if (getRecipesJob != null) return

        _uiState.update { it.copy(isLoading = true) }

        getRecipesJob = viewModelScope.launch(dispatcher) {
            // Fetch latest recipes and update recipe list
            // TODO(recipes data): add more recipes and update this to use pagination
            val result = repository.getAll()

            // Currently this is not a suspending function, as it returns a static list
            val categories = repository.getCategories()

            // Update UI state with result of query
            if (result != null) {
                _uiState.update {
                    HomeUiState(
                        isLoading = false,
                        recipes = result,
                        categories = categories
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessages = listOf(R.string.recipe_query_error)
                    )
                }
            }
        }

        getRecipesJob = null
    }
}