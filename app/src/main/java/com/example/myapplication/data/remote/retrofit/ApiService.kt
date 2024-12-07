package com.example.myapplication.data.remote.retrofit

import com.example.myapplication.data.remote.response.AqiDetailResponse
import com.example.myapplication.data.remote.response.AqiPredictResponse
import com.example.myapplication.data.remote.response.AqiResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/aqi")
    fun getAQI(): Call<AqiResponse>

    @GET("/aqi/detail")
    fun getAQIDetail(): Call<AqiDetailResponse>

    @GET("/aqi/prediction2")
    fun getAQIPredict(): Call<AqiPredictResponse>
}