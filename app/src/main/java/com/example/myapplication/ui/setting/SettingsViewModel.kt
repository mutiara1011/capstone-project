package com.example.myapplication.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    // Example LiveData for potential future use
    private val _language = MutableLiveData<String>().apply {
        value = "Bahasa" // Default language
    }
    val language: LiveData<String> = _language

    private val _theme = MutableLiveData<String>().apply {
        value = "Tema"
    }
    val theme: LiveData<String> = _theme

    private val _notifications = MutableLiveData<String>().apply {
        value = "Notifikasi"
    }
    val notifications: LiveData<String> = _notifications
}
