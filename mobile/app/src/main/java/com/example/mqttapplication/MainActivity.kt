package com.example.mqttapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mqttapplication.screen.MessagesScreen
import com.example.mqttapplication.ui.theme.MqttApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MqttApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val messages = remember {
                        mutableStateListOf<Int>()
                    }
                    val client = MqttClient(
                        serverUri = "tcp://10.0.2.2:1883",
                        appContext = applicationContext,
                        messageArrivedHandler = {
                            messages.add(it)
                        }
                    )

                    LaunchedEffect(Unit) {
                        client.connect(onConnected = {
                            client.subscribe(MqttClient.SubscribeTopic)
                        })
                    }

                    MessagesScreen(messages = messages)
                }
            }
        }
    }
}