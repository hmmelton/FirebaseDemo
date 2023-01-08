package com.hmmelton.firebasedemo.ui.composables.views

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
import com.hmmelton.firebasedemo.data.model.RecipeListItem

/**
 * Composable used for single recipe in HomeScreen list.
 */
@Composable
fun RecipeCard(item: RecipeListItem) {
    Card(elevation = 8.dp) {
        Column(modifier = Modifier.wrapContentHeight()) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(2f)
                    .fillMaxWidth(),
                model = item.thumbnailImage,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Text(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 8.dp
                ),
                text = item.recipe.title,
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
                text = item.recipe.author,
                fontSize = 12.sp,
            )
        }
    }
}
