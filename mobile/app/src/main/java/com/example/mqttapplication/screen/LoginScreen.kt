package com.example.mqttapplication.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.example.mqttapplication.viewmodels.CommonViewModel


@Composable
fun LoginScreen(viewModel: CommonViewModel) {
    val context = LocalContext.current
    LoginScreenInner(onGetDeviceIdButtonClick = {
        try {
            viewModel.acquireDeviceId()
        } catch (e: Exception) {
            Log.e("LoginScreen", "Ошибка отправки запроса на подписку", e)
            Toast.makeText(context, "Произошла ошибка. Попробуйте позже", Toast.LENGTH_SHORT).show()
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenInner(onGetDeviceIdButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold() {
        Column(modifier = modifier.padding(
            start = it.calculateStartPadding(LayoutDirection.Ltr),
            top = it.calculateTopPadding(),
            end = it.calculateEndPadding(LayoutDirection.Ltr),
            bottom = it.calculateBottomPadding())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = onGetDeviceIdButtonClick) {
                Text(text = "Зарегистирироваться в системе")
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreenInner({})
}