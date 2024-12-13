package com.example.myapplication.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.response.acc.RegisterResponse
import com.example.myapplication.data.remote.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SignupViewModel : ViewModel() {

    fun register(username: String, email: String, password: String, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val registerData = mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password
                )

                val jsonData = Gson().toJson(registerData)

                val requestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

                val response: RegisterResponse = ApiConfig.getApiService().register(requestBody)

                if (response.status.code == 200) {
                    callback(true, "Pendaftaran berhasil. Selamat datang, ${response.data?.username}")
                } else {
                    callback(false, "Pendaftaran gagal: ${response.status.message}")
                }

            }
            catch (e: Exception) {
                callback(false, e.message.toString())
            }
        }
    }
}