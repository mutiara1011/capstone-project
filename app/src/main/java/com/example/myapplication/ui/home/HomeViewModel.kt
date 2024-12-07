package com.example.myapplication.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.response.AqiDetailResponse
import com.example.myapplication.data.remote.response.AqiPredictResponse
import com.example.myapplication.data.remote.response.AqiResponse
import com.example.myapplication.data.remote.response.Data
import com.example.myapplication.data.remote.response.DataItem
import com.example.myapplication.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

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

    private val _aqiIndeks = MutableLiveData<Int?>()
    val aqiIndeks: LiveData<Int?> = _aqiIndeks

    private val _aqiDescription = MutableLiveData<String?>()
    val aqiDescription: LiveData<String?> = _aqiDescription

    private val _aqiPredict = MutableLiveData<List<DataItem>>()
    val aqiPredict: LiveData<List<DataItem>> = _aqiPredict

    init {
        getWeather()
        fetchPollutants()
        getPredict()
    }

    private fun fetchPollutants() {
        viewModelScope.launch {
            try {
                val client = ApiConfig.getApiService.getAQIDetail()
                client.enqueue(object : Callback<AqiDetailResponse> {
                    override fun onResponse(
                        call: Call<AqiDetailResponse>,
                        response: Response<AqiDetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val mainData = response.body()?.data?.main
                            _aqiIndeks.postValue(mainData?.aqiIndex)
                            _aqiDescription.postValue(
                                mainData?.aqiIndex?.let { getAQIDescription(it) } ?: "Data tidak tersedia"
                            )
                        } else {
                            Log.e("HomeViewModel", "API Error: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                        Log.e("HomeViewModel", "Error fetching data", t)
                    }
                })
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching data", e)
            }
        }
    }

    private fun getAQIDescription(aqiIndex: Int): String {
        return when (aqiIndex) {
            in 0..50 -> "Baik"
            in 51..100 -> "Sedang"
            in 101..150 -> "Tidak Sehat bagi Kelompok Sensitif"
            in 151..200 -> "Tidak Sehat"
            in 201..300 -> "Sangat Tidak Sehat"
            in 301..500 -> "Berbahaya"
            else -> "Sangat Berbahaya"
        }
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

    fun getPredict() {
        val client = ApiConfig.getApiService.getAQIPredict()
        client.enqueue(object : Callback<AqiPredictResponse> {
            override fun onResponse(
                call: Call<AqiPredictResponse>,
                response: Response<AqiPredictResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "API Response: ${response.body()}")
                    // Menghapus null pada data dan detail
                    val data = response.body()?.data?.filterNotNull()?.mapNotNull { dataItem ->
                        if (dataItem.time.isNullOrEmpty() || dataItem.mainPolutant == null) null
                        else dataItem.copy(
                            detail = dataItem.detail?.filterNotNull()
                        )
                    } ?: emptyList()

                    Log.d("HomeViewModel", "Data Predict: $data")
                    _aqiPredict.value = data
                } else {
                    Log.e("HomeViewModel", "Error: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<AqiPredictResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Error fetching data", t)
            }
        })
    }

}
