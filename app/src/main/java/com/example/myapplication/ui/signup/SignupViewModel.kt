package com.example.myapplication.ui.signup

import android.util.Log
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

                // Mengonversi data ke JSON string menggunakan Gson
                val jsonData = Gson().toJson(registerData)

                // Membuat RequestBody dengan tipe JSON
                val requestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

                // Mengirimkan request ke server dengan body JSON dan menerima response
                val response: RegisterResponse = ApiConfig.getApiService().register(requestBody)

                // Memeriksa apakah status code adalah 200 (sukses)
                if (response.status.code == 200) {
                    // Menyampaikan data pendaftaran yang sukses
                    callback(true, "Pendaftaran berhasil. Selamat datang, ${response.data?.username}")
                } else {
                    // Menyampaikan pesan error dari server
                    callback(false, "Pendaftaran gagal: ${response.status.message}")
                }

            } catch (e: Exception) {
                Log.e("SignupViewModel", "Error: ${e.message}")
                callback(false, e.message.toString())
            }
        }
    }
}