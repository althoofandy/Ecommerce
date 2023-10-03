package com.example.ecommerce.ui.main.menu.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.ecommerce.R
import com.example.ecommerce.model.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var notificationViewModel: NotificationViewModel
    override fun onCreate() {
        super.onCreate()
        notificationViewModel = NotificationViewModel(application)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendNotification(message)
    }

    private fun sendNotification(message: RemoteMessage) {
        val title = message.data["title"] ?: ""
        val body = message.data["body"] ?: ""

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.app_navigation)
            .setDestination(R.id.notificationFragment)
            .createPendingIntent()

        val channelId = getString(R.string.baseline_notifications_24)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())

        val notifEntity = Notification(
            title = title,
            body = message.data["body"] ?: "",
            date = "${message.data["date"]}, ${message.data["time"]}",
            id = UUID.randomUUID().toString(),
            image = message.data["image"] ?: "",
            type = message.data["type"] ?: "",
            isRead = false

        )
        notificationViewModel.addNotification(notifEntity)
    }
}
