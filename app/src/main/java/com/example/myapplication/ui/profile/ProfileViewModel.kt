package com.example.myapplication.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _name = MutableLiveData<String>().apply {
        value = "Syahid Ashabul Fikri"
    }
    val name: LiveData<String> = _name

    private val _joinYear = MutableLiveData<String>().apply {
        value = "Bergabung sejak 2022"
    }
    val joinYear: LiveData<String> = _joinYear
}
