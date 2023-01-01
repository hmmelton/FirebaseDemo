package com.hmmelton.firebasedemo.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data class for a single recipe. The functions [equals] and [hashCode] are overridden as
 * recommended by Android Studio, due to the presence of [Array] constructor parameters.
 */
data class Recipe(
    val id: String,
    val title: String,
    val author: String,
    val servings: Int,
    @SerializedName("total_time_min")
    val totalTimeMin: Int,
    @SerializedName("photo_main")
    val photoMainUri: String,
    @SerializedName("photo_thumbnail")
    val photoThumbnailUri: String,
    val categories: Array<String>,
    val ingredients: Array<String>,
    val instructions: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (servings != other.servings) return false
        if (totalTimeMin != other.totalTimeMin) return false
        if (photoMainUri != other.photoMainUri) return false
        if (photoThumbnailUri != other.photoThumbnailUri) return false
        if (!categories.contentEquals(other.categories)) return false
        if (!ingredients.contentEquals(other.ingredients)) return false
        if (!instructions.contentEquals(other.instructions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + servings.hashCode()
        result = 31 * result + totalTimeMin.hashCode()
        result = 31 * result + photoMainUri.hashCode()
        result = 31 * result + photoThumbnailUri.hashCode()
        result = 31 * result + categories.contentHashCode()
        result = 31 * result + ingredients.contentHashCode()
        result = 31 * result + instructions.contentHashCode()
        return result
    }
}
