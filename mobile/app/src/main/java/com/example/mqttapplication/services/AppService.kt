package com.example.mqttapplication.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.util.UUID

class AppService(private val serverUrl: String, private val client: HttpClient = defaultClient) {

    suspend fun getDeviceId(): UUID {
        val registerUrl = URLBuilder(serverUrl).apply {
            this.encodedPath = subscribeUrlPath
        }.build()
        val response = client.post(registerUrl).body<GetDeviceIdResponseDto>()
        return UUID.fromString(response.deviceId)
    }

    companion object {
        val defaultClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json()
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }

        const val subscribeUrlPath = "/android/subscribe"
    }
}

@Serializable
data class GetDeviceIdResponseDto(val deviceId: String)