package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.remote.response.AqiResponse
import com.example.myapplication.data.remote.response.Data
import com.example.myapplication.data.remote.retrofit.ApiConfig
import com.example.myapplication.ui.user.PollutantData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _highestPollutant = MutableLiveData<PollutantData?>()
    val highestPollutant: LiveData<PollutantData?> = _highestPollutant

    fun updateHighestPollutant(pollutant: PollutantData?) {
        _highestPollutant.value = pollutant
    }

    private val _location = MutableLiveData("Jakarta")
    val location: LiveData<String> = _location

    private val _date = MutableLiveData(
        SimpleDateFormat("EEEE, dd MMMM", Locale("in", "ID")).format(Date())
    )
    val date: LiveData<String> = _date

    private val _time = MutableLiveData(
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    )
    val time: LiveData<String> = _time

    private val _aqi = MutableLiveData<Data?>()
    val aqi: LiveData<Data?> = _aqi

    init {
        getWeather()
    }

    private fun getWeather() {
        val client = ApiConfig.getApiService.getAQI()
        client.enqueue(object : Callback<AqiResponse> {
            override fun onResponse(
                call: Call<AqiResponse>,
                response: Response<AqiResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "API Response: ${response.body()}")
                    _aqi.value = response.body()?.data
                } else {
                    Log.e("HomeViewModel", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Error fetching data", t)
            }
        })
    }
}
