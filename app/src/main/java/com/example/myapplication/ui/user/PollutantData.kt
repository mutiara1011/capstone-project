package com.example.myapplication.ui.user

data class PollutantData(
    val index: String,
    val description: String,
    val title: String,
    val details: String
) {
    fun getAQIDescription(): String {
        val indexValue = index.toIntOrNull() ?: 0
        return when (indexValue) {
            in 0..50 -> "Baik"
            in 51..100 -> "Sedang"
            in 101..150 -> "Tidak Sehat bagi Kelompok Sensitif"
            in 151..200 -> "Tidak Sehat"
            in 201..300 -> "Sangat Tidak Sehat"
            in 301 .. 500 -> "Berbahaya"
            else -> " Sangat Berbahaya"
        }
    }
}

