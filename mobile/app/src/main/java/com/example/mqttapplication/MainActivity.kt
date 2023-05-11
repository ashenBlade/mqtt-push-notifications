package com.example.mqttapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.mqttapplication.models.Message
import com.example.mqttapplication.screen.MessagesScreen
import com.example.mqttapplication.services.MqttClient
import com.example.mqttapplication.ui.theme.MqttApplicationTheme

private const val channelId = "channel1"

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MqttApplicationTheme {
                val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    channelId,
                    "Sample channel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    setAllowBubbles(true)
                    this.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }

                manager.createNotificationChannel(channel)

                val messages = remember {
                    mutableStateListOf<Message>()
                }

                val mqtt = MqttClient(
                    serverUri = "tcp://10.0.2.2:1883",
                    appContext = applicationContext,
                    messageArrivedHandler = {
                        messages.add(it)

                        with(manager) {
                            val notification = NotificationCompat.Builder(applicationContext, channel.id)
                                .setContentTitle(it.Title)
                                .setContentText(it.Body)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)

                            if (ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                                Log.e("MainActivity", "Нет прав публиковать оповещения. Требуемое право ${Manifest.permission.POST_NOTIFICATIONS}")
                                return@with
                            }
                            notify(it.Id.hashCode(), notification.build())
                        }
                    }
                )
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {

                    LaunchedEffect(Unit) {
                        mqtt.connect(onConnected = {
                            mqtt.subscribe(MqttClient.SubscribeTopic)
                        })
                    }

                    MessagesScreen(messages = messages)
                }
            }
        }
    }
}