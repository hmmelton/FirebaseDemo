package com.hmmelton.firebasedemo.ui.composables.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hmmelton.firebasedemo.R
import com.hmmelton.firebasedemo.ui.composables.RequestNotificationPermission
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme
import com.hmmelton.firebasedemo.ui.viewmodels.MainViewModel

@Composable
fun HomeScreen(
    onSignedOut: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
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
        Button(
            onClick = {
                viewModel.signOut()
                onSignedOut()
            }
        ) {
            Text(stringResource(R.string.btn_sign_out))
        }

        // If SDK version >= 33, we must request runtime permission for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RequestNotificationPermission()
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