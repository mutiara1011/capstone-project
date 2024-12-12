package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.DataItem
import com.example.myapplication.databinding.ItemHourBinding

class HomeAdapter : ListAdapter<DataItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    inner class MyViewHolder(private val binding: ItemHourBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            binding.time.text = dataItem.time ?: "-"
            val aqiIndex = dataItem.mainPolutant?.aqiIndex ?: 0
            binding.indexPredict.text = aqiIndex.toString()

            binding.root.setCardBackgroundColor(getCardColor(aqiIndex))
            binding.details.text = dataItem.mainPolutant?.aqiIndex?.let {
                getAQIDescription(it)
            } ?: binding.root.context.getString(R.string.data_not_available)
        }

        private fun getCardColor(aqiIndex: Int): Int {
            return when (aqiIndex) {
                in 0..50 -> android.graphics.Color.parseColor("#77C9B3")
                in 51..100 -> android.graphics.Color.parseColor("#FFCC80")
                in 101..150 -> android.graphics.Color.parseColor("#FFAB9D")
                in 151..200 -> android.graphics.Color.parseColor("#F58D91")
                in 201..300 -> android.graphics.Color.parseColor("#C59DD9")
                else -> android.graphics.Color.parseColor("#B1B1B1")
            }
        }

        private fun getAQIDescription(aqiIndex: Int): String {
            val context = binding.root.context
            return when (aqiIndex) {
                in 0..50 -> context.getString(R.string.aqi_good)
                in 51..100 -> context.getString(R.string.aqi_moderate)
                in 101..150 -> context.getString(R.string.aqi_sensitive_unhealthy)
                in 151..200 -> context.getString(R.string.aqi_unhealthy)
                in 201..300 -> context.getString(R.string.aqi_very_unhealthy)
                else -> context.getString(R.string.aqi_hazardous)
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
