package com.example.myapplication.ui.home

import com.example.myapplication.data.remote.response.DailyDataItem
import com.example.myapplication.data.remote.response.DataItem

sealed class ItemType {
    data class HourItem(val data: DataItem) : ItemType()
    data class DayItem(val data: DailyDataItem) : ItemType()
}