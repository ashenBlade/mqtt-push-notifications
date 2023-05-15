package com.example.mqttapplication.services

import android.app.Application
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mqttapplication.R
import com.example.mqttapplication.models.Message

@RequiresApi(Build.VERSION_CODES.O)
class CustomNotificationManger(
    private val manager: NotificationManager,
    private val notificationBuilder: (Message) -> Notification
) {
    fun notify(message: Message) {
        val notification = notificationBuilder(message)
        manager.notify(message.Id.hashCode(), notification)
    }

    companion object {
        fun forContext(context: Context, ): CustomNotificationManger {
            val manager: NotificationManager = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, "Уведомления", NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE

                }
            manager.createNotificationChannel(channel)
            return CustomNotificationManger(manager) {
                return@CustomNotificationManger NotificationCompat.Builder(context, channel.id)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(it.Body)
                    .setContentTitle(it.Title)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(getPriority(it.PushImportance))
                    .build()
            }
        }

        const val channelId = "Notifications"

        private fun getPriority(importance: Int) = when (importance) {
            0 -> NotificationCompat.PRIORITY_MIN
            1 -> NotificationCompat.PRIORITY_LOW
            2 -> NotificationCompat.PRIORITY_HIGH
            3 -> NotificationCompat.PRIORITY_MAX
            else -> NotificationCompat.PRIORITY_DEFAULT
        }
    }
}