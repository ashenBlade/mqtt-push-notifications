package com.example.mqttapplication

import android.content.Context
import android.util.Log
import com.example.mqttapplication.models.Message
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.lang.NullPointerException

class MqttClient(private val serverUri: String,
                 private val appContext: Context,
                 private val messageArrivedHandler: (Message) -> Unit) {
    private lateinit var mqttClient: MqttAndroidClient

    fun connect(onConnected: () -> Unit = {}) {
        mqttClient = MqttAndroidClient(appContext, serverUri, ClientId, MemoryPersistence())
//        mqttClient.setCallback(object: MqttCallbackExtended {
//            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
//                Log.d(LogTag, "Соединение с сервером $serverURI установлено")
//            }
//
//            override fun connectionLost(cause: Throwable?) {
//                Log.d(LogTag, "Соединение с сервером потеряно", cause)
//            }
//
//            override fun messageArrived(topic: String?, message: MqttMessage?) {
//                if (message == null) {
//                    Log.w(LogTag, "Нагрузка в сообщении - null")
//                    return
//                }
//            }
//
//            override fun deliveryComplete(token: IMqttDeliveryToken?) {
//                Log.d(LogTag, "Доставка завершена. Токен: $token")
//            }
//        })
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
                val deserialized: Message
                try {
                    deserialized = Json.decodeFromString(message.payload.toString(Charsets.UTF_8))
                } catch (e: Exception) {
                    Log.e(LogTag, "Ошибка десериализации сообщения")
                    return@subscribe
                }
                messageArrivedHandler(deserialized)
            }
        } catch (e: MqttException) {
            Log.e(LogTag, "Ошибка во время подписки на топик $topic", e)
        } catch (e: NullPointerException) {
            Log.e(LogTag, "Неизвестная ошибка")
        }
    }

    companion object {
        // https://stackoverflow.com/a/70595131/14109140
        private fun littleEndianConversion(paddedArray: ByteArray): Int {
            return (((paddedArray[3].toULong() and 0xFFu) shl 24) or
                    ((paddedArray[2].toULong() and 0xFFu) shl 16) or
                    ((paddedArray[1].toULong() and 0xFFu) shl 8) or
                    (paddedArray[0].toULong() and 0xFFu)).toInt()
        }

        const val LogTag = "AndroidMqttClient"
        const val SubscribeTopic = "/messages"
        const val ClientId = "AndroidClient"
    }
}