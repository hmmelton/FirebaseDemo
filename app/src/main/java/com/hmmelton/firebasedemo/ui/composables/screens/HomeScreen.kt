package com.hmmelton.firebasedemo.ui.composables.screens

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.ui.composables.RequestNotificationPermission
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onSignedOut: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MainTopAppBar {
                coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerContent = {
            MainDrawerContent {
                viewModel.signOut()
                onSignedOut()
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account icon"
            )
            Text(text = stringResource(R.string.welcome_message), fontSize = 30.sp)

            // If SDK version >= 33, we must request runtime permission for notifications
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                RequestNotificationPermission()
            }
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
        HomeScreen(onSignedOut = {})
    }
}