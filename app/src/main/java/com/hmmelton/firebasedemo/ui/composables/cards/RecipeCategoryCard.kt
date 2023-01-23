package com.hmmelton.firebasedemo.ui.composables.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmmelton.firebasedemo.data.model.RecipeCategory

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeCategoryCard(item: RecipeCategory) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        elevation = 8.dp,
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(60.dp)
                .width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                painter = painterResource(id = item.imageResource),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(id = item.nameResource),
                fontSize = 12.sp
            )
        }
    }
}