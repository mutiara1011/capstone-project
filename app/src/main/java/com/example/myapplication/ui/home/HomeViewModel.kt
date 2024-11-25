package com.example.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ui.user.PollutantData
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private val _highestPollutant = MutableLiveData<String>()
    val highestPollutant: LiveData<String> = _highestPollutant

    fun updateHighestPollutant(pollutant: PollutantData?) {
        _highestPollutant.value = pollutant?.let {
            "${it.index} - ${it.getAQIDescription()}"
        } ?: "Tidak ada data polutan."
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

    private val _suhu = MutableLiveData("30°")
    val suhu: LiveData<String> = _suhu

    private val _angin = MutableLiveData("Kecepatan angin")
    val angin: LiveData<String> = _angin

    private val _anginValue = MutableLiveData("10 km/jam")
    val anginValue: LiveData<String> = _anginValue

    private val _kelembapan = MutableLiveData("Kelembapan")
    val kelembapan: LiveData<String> = _kelembapan

    private val _kelembapanValue = MutableLiveData("85%")
    val kelembapanValue: LiveData<String> = _kelembapanValue
}
