package com.hmmelton.firebasedemo.data.model

import com.hmmelton.firebasedemo.binding.NoArg

/**
 * Data class for a single recipe. The functions [equals] and [hashCode] are overridden as
 * recommended by Android Studio, due to the presence of [Array] constructor parameters.
 */
@NoArg
data class Recipe(
    val uid: String,
    val title: String,
    val author: String,
    val servings: Int,
    val totalTimeMin: Int,
    val photoMainUri: String,
    val photoThumbnailUri: String,
    val categories: List<String>,
    val ingredients: List<String>,
    val directions: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (uid != other.uid) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (servings != other.servings) return false
        if (totalTimeMin != other.totalTimeMin) return false
        if (photoMainUri != other.photoMainUri) return false
        if (photoThumbnailUri != other.photoThumbnailUri) return false
        if (categories != other.categories) return false
        if (ingredients != other.ingredients) return false
        if (directions != other.directions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + servings.hashCode()
        result = 31 * result + totalTimeMin.hashCode()
        result = 31 * result + photoMainUri.hashCode()
        result = 31 * result + photoThumbnailUri.hashCode()
        result = 31 * result + categories.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + directions.hashCode()
        return result
    }

    override fun toString(): String {
        return """
            Recipe(
                uid='$uid',
                title='$title',
                author='$author',
                servings=$servings,
                totalTimeMin=$totalTimeMin,
                photoMainUri='$photoMainUri',
                photoThumbnailUri='$photoThumbnailUri',
                categories=$categories,
                ingredients=$ingredients,
                instructions=$directions
            )
        """.trimIndent()
    }
}
