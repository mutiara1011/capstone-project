package com.example.myapplication.data.remote.response

import com.google.gson.annotations.SerializedName

data class AqiPredictResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: Statues? = null
)

data class Statues(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class MainPolutant(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int? = null,

	@field:SerializedName("polutant_type")
	val polutantType: String? = null,

	@field:SerializedName("concentrate")
	val concentrate: String? = null
)

data class DetailItems(

	@field:SerializedName("aqi_index")
	val aqiIndex: Int? = null,

	@field:SerializedName("polutant_type")
	val polutantType: String? = null,

	@field:SerializedName("concentrate")
	val concentrate: String? = null
)

data class DataItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("main_polutant")
	val mainPolutant: MainPolutant? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("detail")
	val detail: List<DetailItems?>? = null
)
