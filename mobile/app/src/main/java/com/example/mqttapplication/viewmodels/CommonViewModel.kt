package com.example.mqttapplication.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mqttapplication.models.Message
import com.example.mqttapplication.services.AppService
import com.example.mqttapplication.services.AppMqttClient
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import java.util.UUID

class CommonViewModel(val appService: AppService, val client: AppMqttClient): ViewModel() {
    val deviceId = MutableLiveData<UUID>()
    val messages = mutableStateListOf<Message>()

    fun acquireDeviceId() {
        viewModelScope.launch {
            val id = appService.getDeviceId()
            deviceId.value = id
        }
    }


    fun subscribeToMessages() {
        viewModelScope.launch {
            if (client.connect()) {
                val flow = client.subscribe(deviceId.value!!)
                    .cancellable()
                flow.collect {
                    messages.add(it)
                }
            } else {
                throw Exception("Не удалось подключиться к серверу")
            }
        }
    }
}