package com.hmmelton.firebasedemo.ui.composables.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hmmelton.firebasedemo.data.model.RecipeListItem

@Composable
fun RecipeCard(item: RecipeListItem) {
    Row(modifier = Modifier.padding(16.dp).fillMaxWidth().height(50.dp)) {
        AsyncImage(
            model = item.thumbnailImage,
            contentDescription = null
        )
        Column(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 16.dp)) {
            Text(text = item.recipe.title)
            Text(text = item.recipe.author)
        }
    }
}
