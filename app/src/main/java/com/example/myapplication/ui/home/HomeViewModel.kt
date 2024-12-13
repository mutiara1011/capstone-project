package com.example.myapplication.ui.home

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.response.AqiDailyResponse
import com.example.myapplication.data.remote.response.AqiDetailResponse
import com.example.myapplication.data.remote.response.AqiPredictResponse
import com.example.myapplication.data.remote.response.AqiResponse
import com.example.myapplication.data.remote.response.Data
import com.example.myapplication.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SameParameterValue")
class HomeViewModel : ViewModel() {

    private val _location = MutableLiveData("Jakarta")
    val location: LiveData<String> = _location

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> = _time

    private val _aqi = MutableLiveData<Data?>()
    val aqi: LiveData<Data?> = _aqi

    private val _aqiIndex = MutableLiveData<Int?>()
    val aqiIndex: LiveData<Int?> = _aqiIndex

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _itemListHour = MutableLiveData<List<ItemType>>()
    val itemListHour: LiveData<List<ItemType>> = _itemListHour

    private val _itemListDaily = MutableLiveData<List<ItemType>>()
    val itemListDaily: LiveData<List<ItemType>> = _itemListDaily

    init {
        updateTime()
        updateDateLocale()
        getWeather()
        fetchPollutants()
        fetchPredictData()
        fetchDailyData()
    }

    private fun updateTime() {
        val handler = android.os.Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                _time.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updateDateLocale() {
        val locale = Locale.getDefault()
        val format = SimpleDateFormat("EEEE, dd MMMM", locale)
        _date.value = format.format(Date())
    }

    private fun fetchPollutants() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val client = ApiConfig.getApiService().getAQIDetail()
                client.enqueue(object : Callback<AqiDetailResponse> {
                    override fun onResponse(
                        call: Call<AqiDetailResponse>,
                        response: Response<AqiDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val mainData = response.body()?.data?.main
                            _aqiIndex.postValue(mainData?.aqiIndex)
                            _isLoading.postValue(false)
                        }
                    }

                    override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                    }
                })
            } catch (e: Exception) {
                _isLoading.postValue(false)
            }
        }
    }

    private fun getWeather() {
        val client = ApiConfig.getApiService().getAQI()
        _isLoading.postValue(true)
        client.enqueue(object : Callback<AqiResponse> {
            override fun onResponse(
                call: Call<AqiResponse>,
                response: Response<AqiResponse>
            ) {
                if (response.isSuccessful) {
                    _aqi.value = response.body()?.data
                    _isLoading.postValue(false)
                }
            }
            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                _isLoading.postValue(false)
            }
        })
    }

    private fun fetchPredictData() {
        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().getAQIPredict()
        client.enqueue(object : Callback<AqiPredictResponse> {
            override fun onResponse(call: Call<AqiPredictResponse>, response: Response<AqiPredictResponse>) {
                _isLoading.postValue(false)
                if (response.isSuccessful) {
                    val data = response.body()?.data?.mapNotNull { dataItem ->
                        dataItem?.let { ItemType.HourItem(it) }
                    } ?: emptyList()
                    _itemListHour.value = data
                } else {
                    Log.e("AqiPredict", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AqiPredictResponse>, t: Throwable) {
                _isLoading.postValue(false)
                Log.e("AqiPredict", "Failure: ${t.localizedMessage}")
            }
        })
    }

    private fun fetchDailyData() {
        _isLoading.postValue(true)
        val client = ApiConfig.getApiService().getAQIDaily()
        client.enqueue(object : Callback<AqiDailyResponse> {
            override fun onResponse(call: Call<AqiDailyResponse>, response: Response<AqiDailyResponse>) {
                _isLoading.postValue(false)
                if (response.isSuccessful) {
                    val data = response.body()?.data?.map { dailyDataItem ->
                        ItemType.DayItem(dailyDataItem)
                    } ?: emptyList()
                    _itemListDaily.value = data
                    Log.d("AqiPredict", "Data: $data")
                } else {
                    Log.e("AqiPredict", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AqiDailyResponse>, t: Throwable) {
                _isLoading.postValue(false)
                Log.e("AqiPredict", "Failure: ${t.localizedMessage}")
            }
        })
    }

}