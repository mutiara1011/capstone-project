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

    private val _aqiTitle = MutableLiveData<String>().apply {
        value = "Kualitas Udara"
    }
    val aqiTitle: LiveData<String> = _aqiTitle

    private val _recommendationTitle = MutableLiveData<String>().apply {
        value = "Rekomendasi"
    }
    val recommendationTitle: LiveData<String> = _recommendationTitle

    private val _recommendationDescription = MutableLiveData<String>().apply {
        value = "Pastikan untuk menjaga hidrasi dan mengenakan pakaian ringan saat beraktivitas di luar ruangan."
    }
    val recommendationDescription: LiveData<String> = _recommendationDescription

    private val _recommendationTitle2 = MutableLiveData<String>().apply {
        value = "Rekomendasi"
    }
    val recommendationTitle2: LiveData<String> = _recommendationTitle2

    private val _recommendationDescription2 = MutableLiveData<String>().apply {
        value = "Pastikan untuk menjaga hidrasi dan mengenakan pakaian ringan saat beraktivitas di luar ruangan."
    }
    val recommendationDescription2: LiveData<String> = _recommendationDescription2

    private val _aqiTitle2 = MutableLiveData<String>().apply {
        value = "Indeks Kualitas Udara"
    }
    val aqiTitle2: LiveData<String> = _aqiTitle2

    fun fetchPollutants() {
        viewModelScope.launch {
            ApiConfig.getApiService.getAQIDetail().enqueue(object : Callback<AqiDetailResponse> {
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
                    }
                }

                override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                }
            })
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
