package com.example.myapplication.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.retrofit.ApiConfig
import com.example.myapplication.data.remote.response.AqiDetailResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _pollutants = MutableLiveData<List<PollutantData>>()
    val pollutants: LiveData<List<PollutantData>> = _pollutants

    private val _aqiIndex = MutableLiveData<Int>()
    val aqiIndex: LiveData<Int> = _aqiIndex

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    private val _errorState = MutableLiveData<String?>()
    val errorState: LiveData<String?> = _errorState

    private val _qualityIndex = MutableLiveData<String>()
    val qualityIndex: LiveData<String> = _qualityIndex

    private val _textAqi = MutableLiveData<String>()
    val textAqi: LiveData<String> = _textAqi

    fun fetchPollutants() {
        _loadingState.value = true
        _errorState.value = null
        viewModelScope.launch {
            ApiConfig.getApiService().getAQIDetail().enqueue(object : Callback<AqiDetailResponse> {
                override fun onResponse(call: Call<AqiDetailResponse>, response: Response<AqiDetailResponse>) {
                    if (response.isSuccessful) {
                        val details = response.body()?.data?.detail ?: emptyList()
                        val pollutantsList = details.map { detail ->
                            PollutantData(
                                index = detail.aqiIndex.toString(),
                                description = getAQIDescription(detail.aqiIndex ?: 0),
                                title = "${detail.polutantType?.uppercase()} (${getPollutantFullName(detail.polutantType)})",
                                details = "${detail.concentrate} μg/m³"
                            )
                        }
                        _pollutants.postValue(pollutantsList)
                        updateAQI(pollutantsList)
                        _loadingState.postValue(false)
                    } else {
                        _errorState.postValue("Gagal mengambil data: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                    _loadingState.postValue(false)
                    _errorState.postValue("Error jaringan: ${t.localizedMessage}")
                }
            })
        }
    }

    private fun updateAQI(pollutantsList: List<PollutantData>) {
        val highestPollutant = pollutantsList.maxByOrNull { it.index.toIntOrNull() ?: 0 }
        highestPollutant?.let {
            _aqiIndex.postValue(it.index.toIntOrNull() ?: 0)
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

    private fun getPollutantFullName(type: String?): String {
        return when (type) {
            "o3" -> "Ozon"
            "co" -> "Karbon Monoksida"
            "no2" -> "Nitrogen Dioksida"
            "pm10" -> "Materi Partikulat < 10 mikron"
            "pm2.5" -> "Materi Partikulat < 2.5 mikron"
            "so2" -> "Sulfur Dioksida"
            else -> "Tidak Diketahui"
        }
    }
}

