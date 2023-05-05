package com.example.mqttapplication.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MessagesScreen(messages: List<Int>) {
    MessagesScreenInner(messages = messages)
}

@Composable
fun MessagesScreenInner(messages: List<Int>) {
    LazyColumn {
        items(messages) {
            Text(text = "Value: $it")
        }
    }
}

@Preview
@Composable
fun MessagesScreenPreview() {
    MessagesScreenInner(messages = listOf(1, 2, 3))
}