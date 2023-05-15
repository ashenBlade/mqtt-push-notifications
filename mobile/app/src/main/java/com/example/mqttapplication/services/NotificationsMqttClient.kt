package com.example.mqttapplication.services

import android.content.Context
import android.util.Log
import com.example.mqttapplication.models.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppMqttClient(private val serverUri: String,
                    private val appContext: Context) {
    private lateinit var mqttClient: MqttAndroidClient

    suspend fun connect(): Boolean {
        mqttClient = MqttAndroidClient(appContext, serverUri, ClientId, MemoryPersistence())
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        return suspendCoroutine {
            mqttClient.connect(options, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(LogTag, "Соединение успешно установлено")
                    it.resume(true)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(LogTag, "Ошибка во время установки соединения", exception)
                    it.resume(false)
                }
            })
        }
    }

    suspend fun subscribeToAllAndDevice(deviceId: UUID): Flow<Message> {
        // At least once
        val qos = 1

        return callbackFlow {
            // Подписываюсь на пуши для устройства
            val deviceTopic = createDeviceMessagesTopic(deviceId)
            Log.i("NotificationsMqttClient", "Подписываюсь на топик $deviceTopic")
            mqttClient.subscribe(deviceTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(LogTag, "Успешная подписка на топик $deviceTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(LogTag, "Ошибка подписки на топик $deviceTopic", exception)
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

                trySend(deserialized)
            }

            // Подписываюсь на все уведомления
            val allTopic = createAllMessagesTopic()
            mqttClient.subscribe(allTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.i(LogTag, "Успешная подписка на топик $allTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.i(LogTag, "Ошибка подписки на топик $allTopic", exception)
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

                trySend(deserialized)
            }

            this.awaitClose {
                mqttClient.unsubscribe(arrayOf(deviceTopic, allTopic))
            }
        }

    }

    companion object {
        fun createDeviceMessagesTopic(deviceId: UUID): String = "/push/$deviceId"
        fun createAllMessagesTopic() = "/push"
        const val LogTag = "AndroidMqttClient"
        const val ClientId = "AndroidClient"
    }
}