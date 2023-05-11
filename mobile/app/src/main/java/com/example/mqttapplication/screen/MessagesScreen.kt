package com.example.mqttapplication.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mqttapplication.models.Message
import java.util.UUID

@Composable
fun MessagesScreen(messages: List<Message>) {
    MessagesScreenInner(messages = messages)
}

@Composable
fun MessagesScreenInner(messages: List<Message>) {
    LazyColumn {
        items(messages) {
            Text(text = "Message: ${it.Id}, Title: ${it.Title}, Body: ${it.Body}")
        }
    }
}

@Preview
@Composable
fun MessagesScreenPreview() {
    MessagesScreenInner(messages = listOf(Message(UUID.randomUUID().toString(), "Sample title", "Sample title", "Sample template")))
}