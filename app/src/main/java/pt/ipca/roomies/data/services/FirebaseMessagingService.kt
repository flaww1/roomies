package pt.ipca.roomies.data.services

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Handle the data payload, e.g., extract sender, receiver, message content
            // and trigger your chat functionality
            val sender = remoteMessage.data["sender"]
            val content = remoteMessage.data["content"]

            // Broadcast the new message to the app components
            val intent = Intent("NEW_MESSAGE")
            intent.putExtra("sender", sender)
            intent.putExtra("content", content)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }

        // Check if the message contains notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        // Handle token refresh (optional)
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

