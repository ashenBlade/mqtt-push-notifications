package com.example.mqttapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.mqttapplication.models.Message
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
                        mutableStateListOf<Message>()
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