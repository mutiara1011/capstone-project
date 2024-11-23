package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "53 - Sedang"
    }
    val text: LiveData<String> = _text

    private val _location = MutableLiveData<String>().apply {
        value = "Jakarta"
    }
    val location: LiveData<String> = _location

    private val _date = MutableLiveData<String>().apply {
        val currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale("in", "ID")).format(Date())
        value = currentDate
    }
    val date: LiveData<String> = _date

    private val _time = MutableLiveData<String>().apply {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        value = currentTime
    }
    val time: LiveData<String> = _time

    private val _suhu = MutableLiveData<String>().apply {
        value = "30°"
    }
    val suhu: LiveData<String> = _suhu

    private val _angin = MutableLiveData<String>().apply {
        value = "Kecepatan angin"
    }
    val angin: LiveData<String> = _angin

    private val _anginValue = MutableLiveData<String>().apply {
        value = "10 km/jam"
    }
    val anginValue: LiveData<String> = _anginValue

    private val _kelembapan = MutableLiveData<String>().apply {
        value = "Kelembapan"
    }
    val kelembapan: LiveData<String> = _kelembapan

    private val _kelembapanValue = MutableLiveData<String>().apply {
        value = "85%"
    }
    val kelembapanValue: LiveData<String> = _kelembapanValue
}