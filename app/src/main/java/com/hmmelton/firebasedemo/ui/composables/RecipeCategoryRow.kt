package com.hmmelton.firebasedemo.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.ui.composables.cards.RecipeCategoryCard

@Composable
fun RecipeCategoryRow(
    modifier: Modifier = Modifier,
    categories: List<RecipeCategory>
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
    ) {
        items(categories.size) {
            RecipeCategoryCard(
                item = categories[it]
            )
        }
    }
}