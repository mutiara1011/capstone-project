package com.example.myapplication.data.remote.response.acc

import com.example.myapplication.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class UserRepository private constructor(
    private val apiService: ApiService
) {

    suspend fun login(username: String, password: String): LoginResponse {
        val json = """
    {
        "username": "$username",
        "password": "$password"
    }
""".trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        return apiService.login(requestBody)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }
}