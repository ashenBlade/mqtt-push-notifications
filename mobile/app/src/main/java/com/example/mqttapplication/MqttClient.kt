package com.example.mqttapplication

import android.content.Context
import android.util.Log
import kotlinx.coroutines.coroutineScope
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.NullPointerException

class MqttClient(private val serverUri: String,
                 private val appContext: Context,
                 private val messageArrivedHandler: (Int) -> Unit) {
    private lateinit var mqttClient: MqttAndroidClient

    fun connect(onConnected: () -> Unit = {}) {
        mqttClient = MqttAndroidClient(appContext, serverUri, ClientId, MemoryPersistence())
        mqttClient.setCallback(object: MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.d(LogTag, "Соединение с сервером $serverURI установлено")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(LogTag, "Соединение с сервером потеряно", cause)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                if (message == null) {
                    Log.w(LogTag, "Нагрузка в сообщении - null")
                    return
                }
                val value = littleEndianConversion(message.payload)
                messageArrivedHandler(value)
                Log.i(LogTag, "Получено сообщение: $value")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(LogTag, "Доставка завершена. Токен: $token")
            }
        })
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        try {
            mqttClient.connect(options, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(LogTag, "Соединение успешно установлено")
                    onConnected()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(LogTag, "Ошибка во время установки соединения", exception)
                }
            })
        } catch (e: MqttException) {
            Log.e(LogTag, "Ошибка соединения с брокером", e)
        }
    }

    fun subscribe(topic: String = SubscribeTopic, onSubscribed: () -> Unit = {}) {
        try {
            // At least once
            val qos = 1
            mqttClient.subscribe(topic, qos, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(LogTag, "Успешная подписка на топик")
                    onSubscribed()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(LogTag, "Ошибка подписки на топик", exception)
                }
            }) { t, message ->
                Log.d(LogTag, "Получено сообщение из топика $t")
                val i: Int
                try {
                    i = littleEndianConversion(message.payload)
                } catch (e: Exception) {
                    Log.e(LogTag, "Ошибка во время конвертации массива байтов в Int")
                    return@subscribe
                }
                messageArrivedHandler(i)
            }
//            token.waitForCompletion()
        } catch (e: MqttException) {
            Log.e(LogTag, "Ошибка во время подписки на топик $topic", e)
        } catch (e: NullPointerException) {
            Log.e(LogTag, "Неизвестная ошибка")
        }
    }

    companion object {
        // https://stackoverflow.com/a/56873275/14109140
        private fun littleEndianConversion(bytes: ByteArray): Int {
            var result = 0
            for (i in bytes.indices) {
                result = result or (bytes[i].toInt() shl (8 * i))
            }
            return result
        }

        const val LogTag = "AndroidMqttClient"
        const val SubscribeTopic = "/integers"
        const val ClientId = "AndroidClient";
    }
}