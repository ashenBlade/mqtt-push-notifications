package com.example.mqttapplication.screen

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mqttapplication.R
import com.example.mqttapplication.models.Message
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessagesScreen(messages: List<Message>) {
    MessagesScreenInner(messages = messages)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessagesScreenInner(messages: List<Message>) {
    LazyColumn {
        items(messages) {
            Text(text = "Пришло новое оповещение!. Id: ${it.Id}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MessagesScreenPreview() {
    MessagesScreenInner(messages = listOf(Message(UUID.randomUUID().toString(), "Sample title", "Sample title", "Sample template")))
}