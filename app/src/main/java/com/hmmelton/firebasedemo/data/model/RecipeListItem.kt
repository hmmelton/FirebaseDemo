package com.hmmelton.firebasedemo.data.model

/**
 * Data class used to display recipe information in a list
 */
data class RecipeListItem(
    val recipe: Recipe,
    val thumbnailImage: String?
)
