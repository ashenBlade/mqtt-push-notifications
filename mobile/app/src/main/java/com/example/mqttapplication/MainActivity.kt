package com.example.mqttapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mqttapplication.screen.LoginScreen
import com.example.mqttapplication.screen.MessagesScreen
import com.example.mqttapplication.services.AppService
import com.example.mqttapplication.services.AppMqttClient
import com.example.mqttapplication.services.CustomNotificationManger
import com.example.mqttapplication.ui.theme.MqttApplicationTheme
import com.example.mqttapplication.viewmodels.CommonViewModel

private const val channelId = "channel1"

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MqttApplicationTheme {
                val mqttClient = AppMqttClient(serverUri = "tcp://10.0.2.2:1883", appContext = LocalContext.current)
                val notificationManager = CustomNotificationManger.forContext(LocalContext.current)
                val appService = AppService("http://10.0.2.2:5000")
                val viewModel = CommonViewModel(appService = appService, client = mqttClient, manager = notificationManager)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val controller = rememberNavController()
                    NavHost(navController = controller, startDestination = "auth") {
                        composable("auth") {
                            viewModel.deviceId.observe(LocalLifecycleOwner.current) {
                                controller.navigate("main")
                            }
                            LoginScreen(viewModel = viewModel)
                        }

                        composable("main") {
                            LaunchedEffect(Unit) {
                                viewModel.subscribeToMessages()
                            }
                            MessagesScreen(messages = viewModel.messages)
                        }
                    }
                }
            }
        }
    }
}