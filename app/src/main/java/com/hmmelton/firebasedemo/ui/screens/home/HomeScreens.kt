package com.hmmelton.firebasedemo.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.ui.composables.RecipeCategoryRow
import com.hmmelton.firebasedemo.ui.composables.cards.RecipeCard
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme

@Composable
fun HomeScreenWithRecipes(
    uiState: HomeUiState,
    onRecipeClick: (Recipe) -> Unit,
    onCategoryClick: (RecipeCategory) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp),
                elevation = 8.dp,
                backgroundColor = MaterialTheme.colors.secondaryVariant
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.requiredSize(50.dp),
                        painter = painterResource(id = R.drawable.ic_menu_book),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = stringResource(id = R.string.header_text),
                        color = Color.White,
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Categories label
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.categories_label),
                style = MaterialTheme.typography.h5,
            )
        }

        // Categories row
        item {
            RecipeCategoryRow(
                categories = uiState.categories,
                onCategoryClicked = onCategoryClick
            )
        }
        item {
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 8.dp
            )
        }

        // Recipes list
        items(
            items = uiState.recipes,
            key = { recipe ->
                recipe.uid
            }
        ) { recipe ->
            RecipeCard(recipe = recipe) { onRecipeClick(it) }
        }
    }
}

@Composable
fun HomeScreenEmpty(padding: PaddingValues) {
    // show this column if the recipes list is empty
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.ic_menu_book),
            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f),
            contentDescription = ""
        )
        Text(
            text = stringResource(id = R.string.no_recipes),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenEmptyPreview() {
    FirebaseDemoTheme {
        HomeScreenEmpty(PaddingValues(16.dp))
    }
}