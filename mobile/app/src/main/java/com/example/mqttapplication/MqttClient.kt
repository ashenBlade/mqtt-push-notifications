package com.example.mqttapplication

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttClient(private val serverUri: String = "tcp://10.0.2.2:1883", private val appContext: Context) {
    private lateinit var mqttClient: MqttAndroidClient

    suspend fun connect() {
        mqttClient = MqttAndroidClient(appContext, serverUri, ClientId)
        mqttClient.setCallback(object: MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {

            }

            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                
            }
        })
    }

    companion object {
        const val TAG = "AndroidMqttClient"
        const val SubscribeTopic = "/integers"
        const val ClientId = "AndroidClient";
    }
}