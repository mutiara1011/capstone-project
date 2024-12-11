package com.example.myapplication.data.remote.response.acc

import com.example.myapplication.data.remote.response.acc.pref.UserPreference
import com.example.myapplication.data.remote.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun login(username: String, password: String): LoginResponse {
        val json = """
    {
        "username": "$username",
        "password": "$password"
    }
""".trimIndent()

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        return apiService.login(requestBody)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}