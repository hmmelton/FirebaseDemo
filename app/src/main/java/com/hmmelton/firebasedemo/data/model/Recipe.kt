package com.hmmelton.firebasedemo.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Data class for a single recipe. The functions [equals] and [hashCode] are overridden as
 * recommended by Android Studio, due to the presence of [Array] constructor parameters.
 */
data class Recipe(
    val title: String,
    val author: String,
    val category: String,
    val ingredients: Array<String>,
    val instructions: Array<String>,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (title != other.title) return false
        if (author != other.author) return false
        if (category != other.category) return false
        if (!ingredients.contentEquals(other.ingredients)) return false
        if (!instructions.contentEquals(other.instructions)) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + ingredients.contentHashCode()
        result = 31 * result + instructions.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}
