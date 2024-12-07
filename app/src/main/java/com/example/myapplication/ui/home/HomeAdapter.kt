package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.remote.response.DataItem
import com.example.myapplication.databinding.ItemHourBinding

class HomeAdapter : ListAdapter<DataItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    inner class MyViewHolder(private val binding: ItemHourBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            binding.time.text = dataItem.time ?: "-"
            val aqiIndex = dataItem.mainPolutant?.aqiIndex ?: 0
            binding.indexPredict.text = aqiIndex.toString()

            // Mengubah warna CardView berdasarkan AQI Index
            binding.root.setCardBackgroundColor(getCardColor(aqiIndex))
            binding.details.text = dataItem.mainPolutant?.aqiIndex?.let { getAQIDescription(it) } ?: "Data tidak tersedia"
        }

        private fun getCardColor(aqiIndex: Int): Int {
            return when (aqiIndex) {
                in 0..50 -> android.graphics.Color.parseColor("#8DC63F") // Green
                in 51..100 -> android.graphics.Color.parseColor("#FFC107") // Yellow
                in 101..150 -> android.graphics.Color.parseColor("#F09319") // Orange
                in 151..200 -> android.graphics.Color.parseColor("#FF4C4C") // Red
                in 201..300 -> android.graphics.Color.parseColor("#AB47BC") // Dark Red
                else -> android.graphics.Color.parseColor("#8D6E63") // Darker Red
            }
        }

        private fun getAQIDescription(aqiIndex: Int): String {
            return when (aqiIndex) {
                in 0..50 -> "Baik"
                in 51..100 -> "Sedang"
                in 101..150 -> "Tidak Sehat bagi Kelompok Sensitif"
                in 151..200 -> "Tidak Sehat"
                in 201..300 -> "Sangat Tidak Sehat"
                else -> "Berbahaya"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataItem = getItem(position)
        if (dataItem != null) {
            Log.d("HomeAdapter", "Binding data: $dataItem")
            holder.bind(dataItem)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.date == newItem.date && oldItem.time == newItem.time
            }

            override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
