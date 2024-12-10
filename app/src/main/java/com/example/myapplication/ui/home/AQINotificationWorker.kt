package com.example.myapplication.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplication.R

class AQINotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val aqiIndex = getAQI() // Ambil AQI yang disimpan
        if (aqiIndex >= 40) {
            sendAqiAlertNotification(aqiIndex) // Kirim notifikasi jika AQI lebih dari 40
        }
        return Result.success()
    }

    private fun getAQI(): Int {
        // Ambil AQI yang sudah disimpan dalam SharedPreferences atau Database
        val sharedPreferences = applicationContext.getSharedPreferences("AQIData", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("aqi_index", 0) // Mengambil AQI yang disimpan
    }

    private fun sendAqiAlertNotification(aqiIndex: Int) {
        val context = applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Membuat channel untuk notifikasi di Android 8.0 ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "aqi_alert_channel", // ID channel
                "AQI Alert", // Nama channel
                NotificationManager.IMPORTANCE_HIGH // Prioritas channel
            ).apply {
                description = "Channel for AQI alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alert")
        val notification = NotificationCompat.Builder(context, "aqi_alert_channel")
            .setSmallIcon(R.drawable.ic_warning) // Ganti dengan ikon yang sesuai
            .setContentTitle("Warning: High AQI!")
            .setContentText("Current AQI: $aqiIndex. Please take precautions.")
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Bunyi, getar, dan lampu
            .setAutoCancel(true) // Menghilangkan notifikasi setelah ditekan
            .build()

        notificationManager.notify(1, notification)
    }
}
