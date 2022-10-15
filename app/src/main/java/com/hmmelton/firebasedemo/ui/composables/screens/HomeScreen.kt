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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hmmelton.firebasedemo.ui.composables.RequestNotificationPermission
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme

@Composable
fun HomeScreen(
    onSignOut: () -> Unit
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
        Text(text = "Welcome!", fontSize = 30.sp)
        Button(onClick = onSignOut) {
            Text("Sign out")
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
        HomeScreen {}
    }
}