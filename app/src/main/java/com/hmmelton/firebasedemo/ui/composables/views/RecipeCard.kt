package com.hmmelton.firebasedemo.ui.composables.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hmmelton.firebasedemo.data.model.RecipeListItem

@Composable
fun RecipeCard(item: RecipeListItem) {
    Card(
        elevation = 8.dp,
        modifier = Modifier.aspectRatio(0.75f)
    ) {
        Column(
            modifier = Modifier.wrapContentHeight()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                model = item.thumbnailImage,
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Fit
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .weight(1f),
                text = item.recipe.title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
