package com.hmmelton.firebasedemo.notifications

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hmmelton.firebasedemo.MainActivity

private const val TAG = "PushNotificationService"

class PushNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "message ID: ${message.messageId}")

        // From notification message (only when app is in foreground)
        val title = message.notification?.title
        val body = message.notification?.body

        // From data message
        val data = message.data
        val dataTitle: String? = data["title"]
        val dataBody: String? = data["body"]

        if (title != null && body != null) {
            sendNotification(title, body)
        } else if (dataTitle != null && dataBody != null) {
            sendNotification(dataTitle, dataBody)
        } else {
            Log.w(TAG, "body or title null")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, token)
    }

    /**
     * This function displays a system notification to the user.
     * @param title Title of system notification
     * @param body Body of system notification
     */
    private fun sendNotification(title: String, body: String) {
        // TODO(notif): Intent action should be configurable
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // API version 31+ requires FLAG_MUTABLE or FLAG_IMMUTABLE
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, flag)

        // TODO(notif): configurable channelID, change icon
        val builder = NotificationCompat.Builder(this, "Default")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            val currentTime = System.currentTimeMillis().toInt()
            notify(currentTime, builder.build())
        }
    }
}