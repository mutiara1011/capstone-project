package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.remote.response.DailyDataItem
import com.example.myapplication.data.remote.response.DataItem
import com.example.myapplication.databinding.ItemDayBinding
import com.example.myapplication.databinding.ItemHourBinding

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: List<ItemType> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DAY -> {
                val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DayViewHolder(binding)
            }
            VIEW_TYPE_HOUR -> {
                val binding = ItemHourBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HourViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayViewHolder -> holder.bind((itemList[position] as ItemType.DayItem).data)
            is HourViewHolder -> holder.bind((itemList[position] as ItemType.HourItem).data)
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is ItemType.DayItem -> VIEW_TYPE_DAY
            is ItemType.HourItem -> VIEW_TYPE_HOUR
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItemList: List<ItemType>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_DAY = 1
        const val VIEW_TYPE_HOUR = 2
    }

    inner class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DailyDataItem) {
            Log.d("HomeAdapter", "DayItem: ${dataItem.date}, AQI: ${dataItem.mainPolutant.aqiIndex}")
            binding.date.text = dataItem.date
            val aqiIndex = dataItem.mainPolutant.aqiIndex
            binding.indexPredict.text = aqiIndex.toString()
            binding.root.setCardBackgroundColor(getCardColor(aqiIndex))
            binding.details.text = dataItem.mainPolutant.aqiIndex.let {
                getAQIDescription(it)
            }
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

    inner class HourViewHolder(private val binding: ItemHourBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(dataItem: DataItem) {
            Log.d("HomeAdapter", "HourItem: Time: ${dataItem.time}, AQI: ${dataItem.mainPolutant?.aqiIndex}")
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
}
