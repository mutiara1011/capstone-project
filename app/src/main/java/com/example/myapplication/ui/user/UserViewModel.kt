package com.example.myapplication.ui.user

import android.util.Log
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

    private val _qualityIndex = MutableLiveData<String>().apply {
        value = "Quality Index"
    }
    val qualityIndex: LiveData<String> = _qualityIndex

    private val _aqiIndex = MutableLiveData<Int>()
    val aqiIndex: LiveData<Int> = _aqiIndex

    private val _textAqi = MutableLiveData<String>().apply {
        value = "Air Quality Index"
    }
    val textAqi: LiveData<String> = _textAqi

    private val _activityRecommendations = MutableLiveData<String>()
    val activityRecommendations: LiveData<String> = _activityRecommendations

    private val _recommendations = MutableLiveData<String>()
    val recommendations: LiveData<String> = _recommendations

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description


    fun fetchPollutants() {
        viewModelScope.launch {
            ApiConfig.getApiService.getAQIDetail().enqueue(object : Callback<AqiDetailResponse> {
                override fun onResponse(call: Call<AqiDetailResponse>, response: Response<AqiDetailResponse>) {
                    if (response.isSuccessful) {
                        val details = response.body()?.data?.detail ?: emptyList()

                        val pollutantsList = details.map { detail ->
                            Log.d("UserViewModel", "Pollutant: ${detail.polutantType}, Index: ${detail.aqiIndex}")

                            PollutantData(
                                index = detail.aqiIndex.toString(),
                                description = getAQIDescription(detail.aqiIndex ?: 0),
                                title = "${detail.polutantType?.uppercase()} (${getPollutantFullName(detail.polutantType)})",
                                details = "${detail.concentrate} μg/m³"
                            )
                        }

                        _pollutants.postValue(pollutantsList)
                        updateRecommendation(pollutantsList)
                    }else {
                        Log.e("UserViewModel", "API Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AqiDetailResponse>, t: Throwable) {
                    Log.e("UserViewModel", "Error fetching data", t)
                }
            })
        }
    }

    private fun updateRecommendation(pollutantsList: List<PollutantData>) {
        val highestPollutant = pollutantsList.maxByOrNull { it.index.toIntOrNull() ?: 0 }
        highestPollutant?.let {
            _aqiIndex.postValue(it.index.toIntOrNull() ?: 0)
            val (activityRecommendations, recommendations, description) = getAQIRecommendation(it.index.toIntOrNull() ?: 0)
            _activityRecommendations.postValue(activityRecommendations)
            _recommendations.postValue(recommendations)
            _description.postValue(description)
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

    fun getAQIRecommendation(aqiValue: Int): Triple<String, String, String> {
        val activityRecommendations = when (aqiValue) {
            in 0..50 -> "Kualitas udara sangat baik. Anda dapat beraktivitas di luar ruangan dengan aman."
            in 51..100 -> "Kualitas udara cukup baik, tetapi hindari aktivitas fisik berat di luar ruangan jika memungkinkan."
            in 101..150 -> "Kurangi aktivitas fisik berat di luar ruangan, terutama bagi kelompok sensitif."
            in 151..200 -> "Disarankan untuk membatasi semua aktivitas luar ruangan. Fokuskan aktivitas di dalam ruangan."
            in 201..300 -> "Kualitas udara sangat buruk. Hindari semua aktivitas di luar ruangan, tetaplah di dalam rumah."
            in 301..500 -> "Kualitas udara sangat berbahaya. Jangan keluar rumah dan pastikan ventilasi udara di dalam ruangan baik."
            else -> "Data indeks kualitas udara tidak valid."
        }

        val recommendations = when (aqiValue) {
            in 0..50 -> "Risiko kesehatan sangat rendah atau tidak ada"
            in 51..100 -> "Risiko ringan pada kelompok sensitif"
            in 101..150 -> "Risiko moderat pada kelompok sensitif"
            in 151..200 -> "Risiko tinggi pada kelompok sensitif"
            in 201..300 -> "Risiko sangat tinggi pada kelompok sensitif"
            in 301..500 -> "Risiko kesehatan berbahaya, tetap di rumah"
            else -> "Data indeks kualitas udara tidak valid"
        }

        val description = when (aqiValue) {
            in 0..50 -> """
                Tidak ada dampak signifikan terhadap kesehatan
                """.trimIndent()
            in 51..100 -> """
                Gejala ringan pada penderita asma seperti batuk atau sesak napas
                Iritasi ringan pada saluran pernapasan
                """.trimIndent()
            in 101..150 -> """
                Peningkatan risiko serangan asma
                Gejala PPOK (Penyakit Paru Obstruktif Kronis) dapat memburuk
                Peningkatan tekanan darah bagi penderita penyakit jantung
                Risiko iritasi mata, hidung, dan tenggorokan pada anak-anak dan lansia
                """.trimIndent()
            in 151..200 -> """
                Serangan asma lebih sering terjadi
                Gejala PPOK dapat semakin parah
                Risiko komplikasi kardiovaskular bagi penderita penyakit jantung
                Anak-anak, lansia, dan ibu hamil sangat rentan terhadap dampak polusi udara
                """.trimIndent()
            in 201..300 -> """
                Risiko kesehatan serius bagi penderita asma dan PPOK, termasuk kebutuhan perawatan medis darurat
                Peningkatan risiko serangan jantung bagi penderita penyakit jantung
                Kerusakan paru-paru jangka pendek bagi anak-anak
                """.trimIndent()
            in 301..500 -> """
                Dampak sangat serius pada saluran pernapasan, terutama pada penderita asma dan PPOK
                Risiko serangan jantung akut
                Potensi kerusakan paru-paru permanen, terutama pada anak-anak dan lansia
                Semua kelompok sangat disarankan untuk tetap berada di dalam rumah
                """.trimIndent()
            else -> "Indeks kualitas udara tidak valid"
        }

        return Triple(activityRecommendations, recommendations, description)
    }

}
