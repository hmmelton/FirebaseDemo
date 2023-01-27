package com.hmmelton.firebasedemo.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.data.model.Recipe
import com.hmmelton.firebasedemo.data.model.RecipeCategory
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(viewModel: HomeViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeRoute(
        uiState = uiState,
        scaffoldState = rememberScaffoldState(),
        onDrawerOpen = {
            coroutineScope.launch {
                drawerState.open()
            }
        },
        onErrorDismissed = { viewModel.removeErrorMessage(it) },
        onSignOutButtonClick = { viewModel.signOut() },
        onRecipeClick = { recipe -> viewModel.onRecipeClicked(recipe) },
        onCategoryClick = { category -> viewModel.onCategoryClicked(category) }
    )
}

@Composable
fun HomeRoute(
    uiState: HomeUiState,
    scaffoldState: ScaffoldState,
    onDrawerOpen: () -> Unit,
    onErrorDismissed: (Int) -> Unit,
    onSignOutButtonClick: () -> Unit,
    onRecipeClick: (Recipe) -> Unit,
    onCategoryClick: (RecipeCategory) -> Unit
) {
    Scaffold(
        topBar = { MainTopAppBar(onDrawerOpen) },
        drawerContent = { MainDrawerContent(onSignOutButtonClick) }
    ) { contentPadding ->
        // If recipes list is not empty, display entries
        if (uiState.recipes.isNotEmpty()) {
            HomeScreenWithRecipes(
                uiState = uiState,
                onRecipeClick = onRecipeClick,
                onCategoryClick = onCategoryClick
            )
        } else {
            // Otherwise, display empty state
            HomeScreenEmpty(padding = contentPadding)
        }
    }

    // Show error messages one at a time in Snackbar
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the first error message in the list
        val errorMessage = remember(uiState) { uiState.errorMessages.first() }

        // Get text for message and action button
        // TODO: Change button to retry
        val errorMessageText = stringResource(errorMessage)
        val errorActionText = stringResource(R.string.btn_ok)

        // This allows us to use the new value of onErrorDismissed, if it changes, without
        // restarting the effect
        val onDismissed by rememberUpdatedState(onErrorDismissed)

        // Launch effect in coroutine, automatically cancelling and restarting effect
        // with new values when any of the observed values change
        LaunchedEffect(errorMessageText, scaffoldState) {
            // Show snackbar
            scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = errorActionText
            )

            onDismissed(errorMessage)
        }
    }
}

@Composable
private fun MainTopAppBar(onNavIconClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    onNavIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open nav drawer"
                )
            }
        }
    )
}

@Composable
private fun MainDrawerContent(onSignOutButtonClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Icon(
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .padding(16.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account icon"
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "User Name",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                text = "user@email.com"
            )
            Divider()
        }
        // TODO(nav drawer): This should be list of items handled by for-each loop
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSignOutButtonClick()
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout_24),
                    contentDescription = "sign out button"
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(R.string.btn_sign_out),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    FirebaseDemoTheme {
        HomeRoute(
            uiState = HomeUiState(),
            scaffoldState = rememberScaffoldState(),
            onDrawerOpen = {},
            onErrorDismissed = {},
            onSignOutButtonClick = {},
            onRecipeClick = {},
            onCategoryClick = {}
        )
    }
}