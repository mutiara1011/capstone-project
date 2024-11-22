package com.example.myapplication.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    // Data untuk AQI (CardView pertama)
    private val _aqiTitle = MutableLiveData<String>().apply {
        value = "Kualitas Udara"
    }
    val aqiTitle: LiveData<String> = _aqiTitle

    private val _aqiIndex = MutableLiveData<String>().apply {
        value = "53" // Contoh nilai AQI
    }
    val aqiIndex: LiveData<String> = _aqiIndex

    private val _description1 = MutableLiveData<String>().apply {
        value = "Sedang"
    }
    val description1: LiveData<String> = _description1

    private val _description2 = MutableLiveData<String>().apply {
        value = "Polutan Utama"
    }
    val description2: LiveData<String> = _description2

    // Data untuk Rekomendasi (CardView kedua)
    private val _recommendationTitle = MutableLiveData<String>().apply {
        value = "Rekomendasi"
    }
    val recommendationTitle: LiveData<String> = _recommendationTitle

    private val _recommendationDescription = MutableLiveData<String>().apply {
        value = "Pastikan untuk menjaga hidrasi dan mengenakan pakaian ringan saat beraktivitas di luar ruangan."
    }
    val recommendationDescription: LiveData<String> = _recommendationDescription

    private val _aqiTitle2 = MutableLiveData<String>().apply {
        value = "Indeks Kualitas Udara"
    }
    val aqiTitle2: LiveData<String> = _aqiTitle2

    // Data untuk AQI O3
    private val _indexO3 = MutableLiveData<String>().apply { value = "53" }
    val indexO3: LiveData<String> = _indexO3

    private val _descriptionO3 = MutableLiveData<String>().apply { value = "Sedang" }
    val descriptionO3: LiveData<String> = _descriptionO3

    private val _titleIndeksO3 = MutableLiveData<String>().apply { value = "O3 (Ozon)" }
    val titleIndeksO3: LiveData<String> = _titleIndeksO3

    private val _detailsO3 = MutableLiveData<String>().apply { value = "31,56 μg/m³" }
    val detailsO3: LiveData<String> = _detailsO3

    // Data untuk AQI CO
    private val _indexCO = MutableLiveData<String>().apply { value = "45" }
    val indexCO: LiveData<String> = _indexCO

    private val _descriptionCO = MutableLiveData<String>().apply { value = "Baik" }
    val descriptionCO: LiveData<String> = _descriptionCO

    private val _titleIndeksCO = MutableLiveData<String>().apply { value = "CO (Karbon Monoksida)" }
    val titleIndeksCO: LiveData<String> = _titleIndeksCO

    private val _detailsCO = MutableLiveData<String>().apply { value = "12,30 μg/m³" }
    val detailsCO: LiveData<String> = _detailsCO

    // Data untuk AQI NO2
    private val _indexNO2 = MutableLiveData<String>().apply { value = "38" }
    val indexNO2: LiveData<String> = _indexNO2

    private val _descriptionNO2 = MutableLiveData<String>().apply { value = "Sedang" }
    val descriptionNO2: LiveData<String> = _descriptionNO2

    private val _titleIndeksNO2 = MutableLiveData<String>().apply { value = "NO2 (Nitrogen Dioksida)" }
    val titleIndeksNO2: LiveData<String> = _titleIndeksNO2

    private val _detailsNO2 = MutableLiveData<String>().apply { value = "20,56 μg/m³" }
    val detailsNO2: LiveData<String> = _detailsNO2

    // Data untuk AQI PM10
    private val _indexPM10 = MutableLiveData<String>().apply { value = "67" }
    val indexPM10: LiveData<String> = _indexPM10

    private val _descriptionPM10 = MutableLiveData<String>().apply { value = "Sedang" }
    val descriptionPM10: LiveData<String> = _descriptionPM10

    private val _titleIndeksPM10 = MutableLiveData<String>().apply { value = "PM10 (Materi partikulat Kurang dari 10 mikron)" }
    val titleIndeksPM10: LiveData<String> = _titleIndeksPM10

    private val _detailsPM10 = MutableLiveData<String>().apply { value = "40,12 μg/m³" }
    val detailsPM10: LiveData<String> = _detailsPM10

    // Data untuk AQI PM2.5
    private val _indexPM25 = MutableLiveData<String>().apply { value = "85" }
    val indexPM25: LiveData<String> = _indexPM25

    private val _descriptionPM25 = MutableLiveData<String>().apply { value = "Tidak Sehat" }
    val descriptionPM25: LiveData<String> = _descriptionPM25

    private val _titleIndeksPM25 = MutableLiveData<String>().apply { value = "PM2.5 (Materi partikulat Kurang dari 2,5 mikron)" }
    val titleIndeksPM25: LiveData<String> = _titleIndeksPM25

    private val _detailsPM25 = MutableLiveData<String>().apply { value = "25,00 μg/m³" }
    val detailsPM25: LiveData<String> = _detailsPM25

    // Data untuk AQI SO2
    private val _indexSO2 = MutableLiveData<String>().apply { value = "30" }
    val indexSO2: LiveData<String> = _indexSO2

    private val _descriptionSO2 = MutableLiveData<String>().apply { value = "Baik" }
    val descriptionSO2: LiveData<String> = _descriptionSO2

    private val _titleIndeksSO2 = MutableLiveData<String>().apply { value = "SO2 (Sulfur Dioksida)" }
    val titleIndeksSO2: LiveData<String> = _titleIndeksSO2

    private val _detailsSO2 = MutableLiveData<String>().apply { value = "10,00 μg/m³" }
    val detailsSO2: LiveData<String> = _detailsSO2
}
