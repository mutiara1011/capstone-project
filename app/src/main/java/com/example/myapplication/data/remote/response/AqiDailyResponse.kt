package com.example.myapplication.data.remote.response

import com.google.gson.annotations.SerializedName

data class AqiDailyResponse(

	@field:SerializedName("data")
	val data: List<DailyDataItem>,

	@field:SerializedName("status")
	val status: DailyStatus
)

data class DailyDetailItem(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int,

	@field:SerializedName("pollutant_type")
	val pollutantType: String,

	@field:SerializedName("concentrate")
	val concentrate: String
)

data class DailyStatus(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("message")
	val message: String
)

data class DailyDataItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("main_polutant")
	val mainPolutant: DailyMainPolutant,

	@field:SerializedName("detail")
	val detail: List<DailyDetailItem>
)

data class DailyMainPolutant(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int,

	@field:SerializedName("pollutant_type")
	val pollutantType: String,

	@field:SerializedName("concentrate")
	val concentrate: String
)
