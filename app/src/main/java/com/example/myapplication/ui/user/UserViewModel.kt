package com.example.myapplication.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
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

    private val _pollutants = MutableLiveData<Map<String, PollutantData>>().apply {
        value = mapOf(
            "O3" to PollutantData("53", "Sedang", "O3 (Ozon)", "31,56 μg/m³"),
            "CO" to PollutantData("45", "Baik", "CO (Karbon Monoksida)", "12,30 μg/m³"),
            "NO2" to PollutantData("38", "Sedang", "NO2 (Nitrogen Dioksida)", "20,56 μg/m³"),
            "PM10" to PollutantData("67", "Sedang", "PM10 (Partikulat < 10 mikron)", "40,12 μg/m³"),
            "PM2.5" to PollutantData("102", "Tidak Sehat", "PM2.5 (Partikulat < 2,5 mikron)", "25,00 μg/m³"),
            "SO2" to PollutantData("30", "Baik", "SO2 (Sulfur Dioksida)", "10,00 μg/m³")
        )
    }
    val pollutants: LiveData<Map<String, PollutantData>> = _pollutants
}
