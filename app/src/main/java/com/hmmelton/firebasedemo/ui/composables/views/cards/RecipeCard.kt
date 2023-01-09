package com.hmmelton.firebasedemo.ui.composables.views.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hmmelton.firebasedemo.data.model.Recipe

/**
 * Composable used for single recipe in HomeScreen list.
 */
@Composable
fun RecipeCard(recipe: Recipe) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.wrapContentHeight()) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(2f)
                    .fillMaxWidth(),
                model = recipe.photoMainUri,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp
                ),
                text = recipe.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
                text = recipe.author,
                fontSize = 12.sp,
            )
        }
    }
}
