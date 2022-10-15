package com.hmmelton.firebasedemo.ui.composables

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.hmmelton.firebasedemo.BuildConfig

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

    // Return early if permission has already been granted
    if (notificationPermissionState.status.isGranted) {
        return
    }

    if (!permissionAlreadyRequested && !notificationPermissionState.status.shouldShowRationale) {
        SideEffect {
            notificationPermissionState.launchPermissionRequest()
        }
    } else if (notificationPermissionState.status.shouldShowRationale) {
        NotificationPermissionDialog {
            // We do not need to use SideEffect here, because the callback function is
            // non-Composable
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