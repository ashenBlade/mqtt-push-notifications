package com.example.mqttapplication.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(val Id: String, val Title: String, val Body: String, val PushImportance: Int)
