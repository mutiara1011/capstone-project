package com.example.myapplication.ui.home

import android.os.Looper
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
import kotlinx.coroutines.delay
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

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> = _time

    private val _aqi = MutableLiveData<Data?>()
    val aqi: LiveData<Data?> = _aqi

    private val _aqiIndeks = MutableLiveData<Int?>()
    val aqiIndeks: LiveData<Int?> = _aqiIndeks

    private val _aqiDescription = MutableLiveData<String?>()
    val aqiDescription: LiveData<String?> = _aqiDescription

    private val _aqiPredict = MutableLiveData<List<DataItem>>()
    val aqiPredict: LiveData<List<DataItem>> = _aqiPredict

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    init {
        getWeather()
        fetchPollutants()
        getPredict()
        updateTime()
    }

    fun fetchData() {
        _loadingState.value = true
        _errorState.value = null
        viewModelScope.launch {
            try {
                _loadingState.value = false
            } catch (e: Exception) {
                _loadingState.value = false
                _errorState.value = e.localizedMessage ?: "Unknown error occurred"
            }
        }
    }

    private fun updateTime() {
        val handler = android.os.Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                _time.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                handler.postDelayed(this, 1000) // Update setiap 1 detik
            }
        })
    }

    private fun fetchPollutants() {
        _loadingState.postValue(true)
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
                            _aqiIndeks.postValue(mainData?.aqiIndex)
                            _aqiDescription.postValue(
                                mainData?.aqiIndex?.let { getAQIDescription(it) } ?: "Data tidak tersedia"
                            )
                            _loadingState.postValue(false)
                        } else {
                            _errorState.value = "Error: ${response.errorBody()?.string()}"
                        }
                    }

                    override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                        _loadingState.postValue(false)
                        _errorState.value = "Failed to fetch pollutants: ${t.localizedMessage}"
                    }
                })
            } catch (e: Exception) {
                _loadingState.postValue(false)
                _errorState.value = "Error"
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
        val client = ApiConfig.getApiService().getAQI()
        _loadingState.postValue(true)
        client.enqueue(object : Callback<AqiResponse> {
            override fun onResponse(
                call: Call<AqiResponse>,
                response: Response<AqiResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "API Response: ${response.body()}")
                    _aqi.value = response.body()?.data
                    _loadingState.postValue(false)
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
        val client = ApiConfig.getApiService().getAQIPredict()
        _loadingState.postValue(true)
        client.enqueue(object : Callback<AqiPredictResponse> {
            override fun onResponse(
                call: Call<AqiPredictResponse>,
                response: Response<AqiPredictResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "API Response: ${response.body()}")
                    val data = response.body()?.data?.filterNotNull()?.mapNotNull { dataItem ->
                        if (dataItem.time.isNullOrEmpty() || dataItem.mainPolutant == null) null
                        else dataItem.copy(
                            detail = dataItem.detail?.filterNotNull()
                        )
                    } ?: emptyList()

                    Log.d("HomeViewModel", "Data Predict: $data")
                    _aqiPredict.value = data
                    _loadingState.postValue(false)

                } else {
                    _loadingState.postValue(false)
                    _errorState.value = "Failed to fetch AQI predictions"
                }
            }
            override fun onFailure(call: Call<AqiPredictResponse>, t: Throwable) {
                _loadingState.postValue(false)
                _errorState.value = "Error: ${t.localizedMessage}"
            }
        })
    }

}
