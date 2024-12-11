package com.example.myapplication.data.remote.retrofit

import com.example.myapplication.data.remote.response.AqiDetailResponse
import com.example.myapplication.data.remote.response.AqiPredictResponse
import com.example.myapplication.data.remote.response.AqiResponse
import com.example.myapplication.data.remote.response.acc.LoginResponse
import com.example.myapplication.data.remote.response.acc.RegisterResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/aqi")
    fun getAQI(): Call<AqiResponse>

    @GET("/aqi/detail")
    fun getAQIDetail(): Call<AqiDetailResponse>

    @GET("/aqi/prediction2")
    fun getAQIPredict(): Call<AqiPredictResponse>

    @POST("/user/register")
    suspend fun register(
        @Body requestBody: RequestBody
    ): RegisterResponse

    @POST("/user/login")
    suspend fun login(
        @Body requestBody: RequestBody
    ): LoginResponse
}