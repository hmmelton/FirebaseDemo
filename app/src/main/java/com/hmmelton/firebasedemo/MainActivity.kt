package com.hmmelton.firebasedemo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hmmelton.firebasedemo.ui.theme.FirebaseDemoTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseDemoTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    HomeScreen()

                    // If SDK version >= 33, we must request runtime permission for notifications
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        RequestNotificationPermission()
                    }
                }
            }
        }

        createNotificationChannel()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Log token
            Log.d(TAG, "Your token is: ${task.result}")
        })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default"
            val descriptionText = "Default channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                "Default",
                name,
                importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun HomeScreen() {
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
    }
}

/**
 * Composable for requesting system notification permissions. Only required for version
 * [Build.VERSION_CODES.TIRAMISU] or greater.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    var permissionAlreadyRequested by rememberSaveable {
        mutableStateOf(false)
    }

    // Notification permission state
    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    ) {
       permissionAlreadyRequested = true
    }

    if (!permissionAlreadyRequested && !notificationPermissionState.status.shouldShowRationale) {
        // Request notification permission
        SideEffect {
            notificationPermissionState.launchPermissionRequest()
        }
    } else if (notificationPermissionState.status.shouldShowRationale) {
        // Display rational for requesting notification permission
        NotificationPermissionDialog {
            notificationPermissionState.launchPermissionRequest()
        }
    } else {
        val context = LocalContext.current
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Display dialog requesting user go to app settings to turn on notification permission
        OpenSettingsDialog {
            context.startActivity(intent)
        }
    }
}

/**
 * This function displays an alert prompting the user for notification permissions
 */
@Composable
private fun NotificationPermissionDialog(
    onConfirm: () -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(true) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text("Permission request") },
            text = {
                Text("Please grant notification permissions. They are important for this " +
                        "application")
                   },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Ok")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text("Deny")
                }
            }
        )
    }
}

/**
 * This function displays an alert prompting the user to allow notification permission from app
 * settings.
 */
@Composable
private fun OpenSettingsDialog(
    onConfirm: () -> Unit
) {
    var openDialog by rememberSaveable { mutableStateOf(true) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text("Open app settings") },
            text = {
                Text("Notification permission has been denied. Please approve notifications " +
                        "from app settings.")
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text("Ok")
                }
            },
            dismissButton = {
                Button(onClick = { openDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    FirebaseDemoTheme {
        HomeScreen()
    }
}